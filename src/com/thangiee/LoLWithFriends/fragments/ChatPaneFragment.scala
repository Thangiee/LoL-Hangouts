package com.thangiee.LoLWithFriends.fragments

import java.util.Date

import android.app.Fragment
import android.os.{Bundle, SystemClock}
import android.view.View.OnClickListener
import android.view._
import android.widget.EditText
import com.dd.CircularProgressButton
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLWithFriends.api.{LoLChat, Summoner}
import com.thangiee.LoLWithFriends.utils.{SummonerUtils, DataBaseHandler}
import com.thangiee.LoLWithFriends.utils.Events.ReceivedMessage
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatPaneFragment extends Fragment with TagUtil {
  private var view: View = _
  private lazy val sendButton = view.findViewById(R.id.btn_send_msg).asInstanceOf[CircularProgressButton]
  private lazy val msgField = view.findViewById(R.id.et_msg_field).asInstanceOf[EditText]
  private lazy val friendName = getArguments.getString("name-key")
  private lazy val messageAdapter = new MessageAdapter(getActivity, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setHasOptionsMenu(true)
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.chat_pane, container, false)

    sendButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = sendMessage()
    })
    sendButton.setIndeterminateProgressMode(true)
    msgField.setHint("send to " + friendName)

    val messageLog = DataBaseHandler.getMessageLog
    messageAdapter.addAll(messageLog) // add all messages
    messageAdapter.setSenderImgUrl(SummonerUtils.profileIconUrl(MyApp.currentUser, MyApp.selectedServer))
    messageAdapter.setRecipientImgUrl(SummonerUtils.profileIconUrl(friendName, MyApp.selectedServer))

    setMessagesRead()
    val messageListView = view.findViewById(R.id.lsv_chat).asInstanceOf[MessagesListView]
    messageListView.setAdapter(messageAdapter)
    messageListView.setBackground(getActivity.getResources.getDrawable(R.drawable.league_dark_blue_bg_pattern))
    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)

    view
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.unregister(this, classOf[ReceivedMessage])
    super.onDestroy()
  }

  def setMessagesRead() {
    for (i <- messageAdapter.getCount-1 to 0 by -1) {
      val msg = messageAdapter.getItem(i)
      if (msg.isRead) return else msg.setIsRead(true).save()
    }
  }

  private def sendMessage() {
    // don't send if blank
    if (msgField.getText.length() == 0) {
      Crouton.makeText(getActivity, "Can't send empty message", Style.INFO).show()
      return
    }

    sendButton.setProgress(50)
//    sendButton.setEnabled(false) // library bug atm
    Future {
      SystemClock.sleep(750)
      if (LoLChat.sendMessage(LoLChat.getFriendByName(friendName).get, msgField.getText.toString)) {
        // if message sent, then save that message to DB
        val msg = new models.Message.MessageBuilder(MESSAGE_TYPE_SENT).text(msgField.getText.toString)
          .date(new Date()).otherPerson(friendName).thisPerson(MyApp.currentUser).isRead(true).build()
        msg.save() // save to DB

        runOnUiThread(messageAdapter.add(msg)) // add to adapter to show the message on the chat
        runOnUiThread(msgField.setText("")) // clear the message field
        runOnUiThread(sendButton.setProgress(100)) // success state
        SystemClock.sleep(150)
        runOnUiThread(sendButton.setProgress(0)) // normal state
        runOnUiThread(sendButton.setEnabled(true))
      } else {  // message failed to send
        runOnUiThread(sendButton.setProgress(-1)) // error state
        SystemClock.sleep(150)
        runOnUiThread(Crouton.makeText(getActivity, "Fail to send message", Style.ALERT).show()) // alert the user
        runOnUiThread(sendButton.setProgress(0)) // normal state
//        runOnUiThread(sendButton.setEnabled(true))
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.menu_delete, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_delete => DataBaseHandler.deleteMessageLog(); messageAdapter.clear()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  def onEventMainThread(event: ReceivedMessage): Unit = {
    info("[*]onEvent: received message from "+event.summoner.name)
    messageAdapter.add(event.msg) // add received message to adapter to show the message on the chat
  }

  private def runOnUiThread(f: => Unit) {
    getActivity.runOnUiThread(new Runnable {
      override def run(): Unit = f
    })
  }
}

object ChatPaneFragment {
  def newInstance(summoner: Summoner): ChatPaneFragment = {
    val bundle = new Bundle()
    bundle.putString("name-key", summoner.name)
    val frag = new ChatPaneFragment
    frag.setArguments(bundle)
    frag
  }
}
