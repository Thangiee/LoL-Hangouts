package com.thangiee.LoLHangouts.fragments

import java.util.Date

import android.media.MediaPlayer
import android.os.{Bundle, SystemClock}
import android.preference.PreferenceManager
import android.view._
import android.widget.EditText
import com.dd.CircularProgressButton
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.{Summoner, LoLChat}
import com.thangiee.LoLHangouts.utils.DataBaseHandler
import com.thangiee.LoLHangouts.utils.Events.ReceivedMessage
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.scaloid.common.AlertDialogBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatPaneFragment extends TFragment {
  private lazy val sendButton = find[CircularProgressButton](R.id.btn_send_msg)
  private lazy val msgField = find[EditText](R.id.et_msg_field)
  private lazy val friendName = getArguments.getString("name-key")
  private lazy val messageAdapter = new MessageAdapter(getActivity, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setHasOptionsMenu(true)
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.chat_pane, container, false)

    sendButton.setOnClickListener((v: View) ⇒ sendMessage())
    sendButton.setIndeterminateProgressMode(true)
    msgField.setHint("send to " + friendName)

    val messageLog = DataBaseHandler.getMessages(appCtx.currentUser, appCtx.activeFriendChat)
    messageAdapter.addAll(messageLog) // add all messages
    messageAdapter.setRegion(appCtx.selectedRegion.toString)
    messageAdapter.setSenderName(appCtx.currentUser)
    messageAdapter.setRecipientName(friendName)

    setMessagesRead()
    val messageListView = find[MessagesListView](R.id.lsv_chat)
    messageListView.setAdapter(messageAdapter)
    messageListView.setBackgroundColor(R.color.my_darker_blue.r2Color)
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
          .date(new Date()).otherPerson(friendName).thisPerson(appCtx.currentUser).isRead(true).build()
        msg.save() // save to DB
        if (isSoundPreferenceOn) MediaPlayer.create(getActivity, R.raw.alert_pm_sent).start() // play sound

        runOnUiThread {
          messageAdapter.add(msg) // add to adapter to show the message on the chat
          msgField.setText("") // clear the message field
          sendButton.setProgress(100) // success state
        }
        SystemClock.sleep(150)
        runOnUiThread {
          sendButton.setProgress(0) // normal state
          sendButton.setEnabled(true)
        }
      } else {  // message failed to send
        runOnUiThread(sendButton.setProgress(-1)) // error state
        SystemClock.sleep(150)
        runOnUiThread {
          Crouton.makeText(getActivity, "Fail to send message", Style.ALERT).show() // alert the user
          sendButton.setProgress(0) // normal state
        }
//        runOnUiThread(sendButton.setEnabled(true))
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.delete, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_delete => confirmDeleteAllMsg()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  def onEventMainThread(event: ReceivedMessage): Unit = {
    info("[*]onEvent: received message from "+event.summoner.name)
    messageAdapter.add(event.msg) // add received message to adapter to show the message on the chat

    // check sound preference before playing sound
    if (isSoundPreferenceOn) MediaPlayer.create(getActivity, R.raw.alert_pm_receive).start()
  }

  private def isSoundPreferenceOn: Boolean = {
    PreferenceManager.getDefaultSharedPreferences(getActivity).getBoolean(R.string.pref_notify_sound.r2String, true)
  }

  private def confirmDeleteAllMsg(): Unit = {
    new AlertDialogBuilder(R.string.dialog_delete_title.r2String, R.string.dialog_delete_message.r2String) {
      positiveButton("Delete", {DataBaseHandler.deleteMessages(appCtx.currentUser, appCtx.activeFriendChat); messageAdapter.clear()})
      negativeButton(android.R.string.cancel.r2String)
    }.show()
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
