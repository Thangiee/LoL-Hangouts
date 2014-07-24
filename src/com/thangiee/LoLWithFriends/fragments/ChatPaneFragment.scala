package com.thangiee.LoLWithFriends.fragments

import java.util.Date

import android.app.Fragment
import android.os.{SystemClock, Bundle}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.EditText
import com.dd.CircularProgressButton
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.DataBaseHandler
import com.thangiee.LoLWithFriends.utils.Events.ReceivedMessage
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Style, Crouton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatPaneFragment extends Fragment {
  private var view: View = _
  lazy val sendButton = view.findViewById(R.id.btn_send_msg).asInstanceOf[CircularProgressButton]
  lazy val msgField = view.findViewById(R.id.et_msg_field).asInstanceOf[EditText]

  lazy val messageAdapter = new MessageAdapter(getActivity, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.chat_pane, container, false)
    EventBus.getDefault.register(this)

    sendButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = sendMessage()
    })
    sendButton.setIndeterminateProgressMode(true)

    val messageListView = view.findViewById(R.id.lsv_chat).asInstanceOf[MessagesListView]
    messageAdapter.addAll(DataBaseHandler.getMessageLog("lolwithfriends"))
    messageListView.setAdapter(messageAdapter)

    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)

    view
  }

  def sendMessage() {
    if (msgField.getText.length() == 0) return // don't send if blank
    sendButton.setProgress(50)
    sendButton.setEnabled(false)
    Future {
      SystemClock.sleep(750)
      if (LoLChat.sendMessage(LoLChat.getFriendByName("lolwithfriends").get, msgField.getText.toString)) {
        // if message sent, then save that message to DB
        val msg = new models.Message.MessageBuilder(MESSAGE_TYPE_SENT).text(msgField.getText.toString)
          .date(new Date()).name("lolwithfriends").build()
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
