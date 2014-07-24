package com.thangiee.LoLWithFriends.services

import java.util.Date

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.{R, MyApplication}
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{Chat, MessageListener}
import org.scaloid.common.{SystemService, SService, UnregisterReceiver}

class ChatService extends SService with UnregisterReceiver with MessageListener with SystemService {
  private lazy val app = ctx.getApplication.asInstanceOf[MyApplication]

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initChatListener(this)
  }

  override def processMessage(chat: Chat, msg: Message): Unit = {
    val summ = LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get

    // save received message to DB
    val m = new models.Message.MessageBuilder(MESSAGE_TYPE_RECEIVED).text(msg.getBody).date(new Date()).name(summ.name).build()
    m.save()

    // show notification when chat pane fragment is not open
    // or the current chat is not with sender of the message
    if (!app.isChatOpen || app.activeFriendChat != summ.name) {
      val builder = new Notification.Builder(ctx)
        .setSmallIcon(R.drawable.mlv__default_avatar)
        .setContentText(summ.name + ": " + m.getText)

      val notification = builder.build()
      notificationManager.notify(1, notification)
      return
    }

    EventBus.getDefault.post(new Events.ReceivedMessage(summ, m))
  }
}
