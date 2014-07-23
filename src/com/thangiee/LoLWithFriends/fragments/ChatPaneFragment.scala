package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.dd.CircularProgressButton
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.utils.DataBaseHandler
import com.thangiee.LoLWithFriends.utils.Events.ReceivedMessage
import de.greenrobot.event.EventBus

class ChatPaneFragment extends Fragment {
  var sendButton: CircularProgressButton = _
  lazy val messageAdapter = new MessageAdapter(getActivity, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    EventBus.getDefault.register(this)
    val view = inflater.inflate(R.layout.chat_pane, container, false)
    sendButton = view.findViewById(R.id.btn_send_msg).asInstanceOf[CircularProgressButton]
    sendButton.setIndeterminateProgressMode(true)
    sendButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = sendMessage()
    })

    val messageListView = view.findViewById(R.id.lsv_chat).asInstanceOf[MessagesListView]
    messageAdapter.addAll(DataBaseHandler.getMessageLog("test"))
    messageListView.setAdapter(messageAdapter)
    view
  }

  def sendMessage() {
    sendButton.setProgress(50)
    Thread.sleep(100)
    sendButton.setProgress(0)
  }

  def onEventMainThread(event: ReceivedMessage): Unit = {
    messageAdapter.add(event.msg)
  }
}
