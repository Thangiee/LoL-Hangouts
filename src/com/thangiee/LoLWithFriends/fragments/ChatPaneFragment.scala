package com.thangiee.LoLWithFriends.fragments

import java.util.Date

import android.app.Fragment
import android.os.{Bundle, SystemClock}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.EditText
import com.dd.CircularProgressButton
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLWithFriends.{MyApp, R}
import com.thangiee.LoLWithFriends.api.{LoLChat, Summoner}
import com.thangiee.LoLWithFriends.utils.DataBaseHandler
import com.thangiee.LoLWithFriends.utils.Events.ReceivedMessage
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatPaneFragment private extends Fragment {
  private var view: View = _
  private lazy val sendButton = view.findViewById(R.id.btn_send_msg).asInstanceOf[CircularProgressButton]
  private lazy val msgField = view.findViewById(R.id.et_msg_field).asInstanceOf[EditText]
  private lazy val friendName = getArguments.getString("name-key")
  private lazy val messageAdapter = new MessageAdapter(getActivity, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.chat_pane, container, false)

    sendButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = sendMessage()
    })
    sendButton.setIndeterminateProgressMode(true)
    msgField.setHint("send to " + friendName)

    val messageListView = view.findViewById(R.id.lsv_chat).asInstanceOf[MessagesListView]
    messageAdapter.addAll(DataBaseHandler.getMessageLog(MyApp.currentUser, friendName))
    messageListView.setAdapter(messageAdapter)

    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)

    view
  }

  override def onResume(): Unit = {
    super.onResume()
    EventBus.getDefault.register(this)
  }

  override def onPause(): Unit = {
    super.onPause()
    EventBus.getDefault.unregister(this, classOf[ReceivedMessage])
  }

  private def sendMessage() {
    if (msgField.getText.length() == 0) return // don't send if blank
    sendButton.setProgress(50)
    sendButton.setEnabled(false)
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
        runOnUiThread(sendButton.setEnabled(true))
      }
    }
  }

  def onEventMainThread(event: ReceivedMessage): Unit = {
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
