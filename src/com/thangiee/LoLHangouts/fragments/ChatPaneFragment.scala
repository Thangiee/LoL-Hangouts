package com.thangiee.LoLHangouts.fragments

import java.util.Date

import android.media.MediaPlayer
import android.os.{Bundle, SystemClock}
import android.view._
import android.widget.EditText
import com.dd.CircularProgressButton
import com.pixplicity.easyprefs.library.Prefs
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.utils.DB
import com.thangiee.LoLHangouts.utils.Events.IncomingMessage
import com.thangiee.LoLHangouts.views.ConfirmDialog
import com.thangiee.common._
import de.greenrobot.event.EventBus

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ChatPaneFragment() extends TFragment {
  private lazy val sendButton = find[CircularProgressButton](R.id.btn_send_msg)
  private lazy val msgField = find[EditText](R.id.et_msg_field)
  private lazy val friendName = getArguments.getString("name-key")
  private lazy val messageAdapter = new MessageAdapter(ctx, 0)
  private lazy val messageListView = find[MessagesListView](R.id.lsv_chat)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setHasOptionsMenu(true)
    view = inflater.inflate(R.layout.chat_pane, container, false)

    sendButton.setOnClickListener((v: View) ⇒ sendMessage())
    sendButton.setIndeterminateProgressMode(true)
    msgField.setHint("send to " + friendName)

    messageAdapter.setRegion(Prefs.getString("region-key", ""))
    messageAdapter.setSenderName(appCtx.currentUser)
    messageAdapter.setRecipientName(friendName)

    messageListView.setAdapter(messageAdapter)
    messageListView.setBackgroundColor(R.color.my_dark_blue.r2Color)

    view
  }

  override def onStart(): Unit = {
    super.onStart()
    appCtx.activeFriendChat = friendName
    getActivity.getActionBar.setTitle(friendName) // set AB title to name of friend in chat with

    EventBus.getDefault.register(this)
    setMessagesRead()

    val messageLog = DB.getMessages(appCtx.currentUser, friendName, R.string.pref_max_msg.pref2Int(20))
    messageAdapter.addAll(messageLog) // add all messages
    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)
  }

  override def onPause(): Unit = {
    appCtx.activeFriendChat = ""
    messageAdapter.clear()
    EventBus.getDefault.unregister(this)
    super.onPause()
  }

  def setMessagesRead(): Unit = {
    DB.getUnreadMessages(appCtx.currentUser, friendName).map(m ⇒ m.setRead(true).save())
  }

  private def sendMessage() {
    // don't send if blank
    if (msgField.getText.length() == 0) {
      "Can't send empty message".croutonInfo()
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
        "Fail to send message".croutonWarn()
        runOnUiThread(sendButton.setProgress(0)) // normal state
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
      case R.id.menu_delete => ConfirmDialog(
        msg = R.string.dialog_delete_message.r2String,
        btnTitle = "Delete",
        code2run = { DB.deleteMessages(appCtx.currentUser, friendName); messageAdapter.clear() }
      ).show()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  def onEventMainThread(event: IncomingMessage): Unit = {
    if (friendName == event.from.name) {  // is the incoming message for the current chat?
      messageAdapter.add(event.msg) // add received message to adapter to show the message on the chat
      // check sound preference before playing sound
      if (isSoundPreferenceOn) MediaPlayer.create(getActivity, R.raw.alert_pm_receive).start()
    }
  }

  private def isSoundPreferenceOn: Boolean = R.string.pref_notify_sound.pref2Boolean(default = true)
}

object ChatPaneFragment {
  def apply(friendName: String): ChatPaneFragment = {
    ChatPaneFragment().args("name-key" → friendName)
  }
}