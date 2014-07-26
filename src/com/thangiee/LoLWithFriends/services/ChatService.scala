package com.thangiee.LoLWithFriends.services

import java.util.Date

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.{R, MyApp}
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{Chat, MessageListener}
import org.scaloid.common.{SystemService, SService, UnregisterReceiver}

class ChatService extends SService with UnregisterReceiver with MessageListener with SystemService {

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initChatListener(this)
  }

  override def processMessage(chat: Chat, msg: Message): Unit = {
    val summ = LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get

    // create Message object with the received chat message
    val m = new models.Message.MessageBuilder(MESSAGE_TYPE_RECEIVED).text(msg.getBody).date(new Date())
      .otherPerson(summ.name).thisPerson(MyApp.currentUser).isRead(true).build()

    // show notification when chat pane fragment is not open
    // or the current chat is not with sender of the message
    if (!MyApp.isChatOpen || MyApp.activeFriendChat != summ.name) {
      m.setIsRead(false) // set to false because user has not seen it
      m.save() // save to DB

      // create notification
      val builder = new Notification.Builder(ctx)
        .setSmallIcon(R.drawable.mlv__default_avatar)
        .setContentText(summ.name + ": " + m.getText)
      val notification = builder.build()
      notificationManager.notify(1, notification)
      EventBus.getDefault.post(new Events.ReceivedMessage(summ, m))
      EventBus.getDefault.postSticky(new Events.RefreshFriendList)    //todo: improve performance by refreshing 1 card
      return
    }

    m.save() // save to DB
    EventBus.getDefault.post(new Events.ReceivedMessage(summ, m))
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)      //todo: improve performance by refreshing 1 card
  }
}
