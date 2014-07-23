package com.thangiee.LoLWithFriends.services

import java.util.Date

import android.content.Intent
import android.os.IBinder
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{Chat, MessageListener}
import org.scaloid.common.{SService, UnregisterReceiver}

class ChatService extends SService with UnregisterReceiver with MessageListener {

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initChatListener(this)
  }

  override def processMessage(chat: Chat, msg: Message): Unit = {
    val summ = LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get

    // save received message to DB
    val m = new models.Message.MessageBuilder(MESSAGE_TYPE_RECEIVED)
      .text(msg.getBody)
      .date(new Date())
      .name(summ.name)
      .build()
    m.save()

    EventBus.getDefault.post(new Events.ReceivedMessage(summ, m))
  }
}
