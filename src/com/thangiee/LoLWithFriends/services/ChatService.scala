package com.thangiee.LoLWithFriends.services

import java.util.Date

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.media.RingtoneManager
import android.os.{Build, IBinder}
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.activities.MainActivity
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events.ClearChatNotification
import com.thangiee.LoLWithFriends.utils.{DataBaseHandler, Events}
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{Chat, MessageListener}
import org.scaloid.common.{SService, SystemService, TagUtil, UnregisterReceiver, info}

import scala.util.Random

class ChatService extends SService with UnregisterReceiver with MessageListener with SystemService with TagUtil {
  private val id = Random.nextInt()

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initChatListener(this)
    EventBus.getDefault.registerSticky(ctx)
  }

  override def onDestroy(): Unit = {
    notificationManager.cancel(id)
    EventBus.getDefault.unregister(this, classOf[ClearChatNotification])
    super.onDestroy()
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
      m.save() // save message to DB

      createNotification(m)

      EventBus.getDefault.post(new Events.RefreshSummonerCard(summ))
      return
    }

    m.save() // save to DB
    EventBus.getDefault.post(new Events.ReceivedMessage(summ, m))
    EventBus.getDefault.post(new Events.RefreshSummonerCard(summ))
  }

  private def createNotification(newestMsg: models.Message) {
    val unReadMsg = DataBaseHandler.getUnReadMessages

    // intent to bring the app to foreground
    val i = new Intent(ctx, classOf[MainActivity])
    val pendingIntent = PendingIntent.getActivity(ctx, 0, i, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

    val builder = new Notification.Builder(getApplicationContext)
      .setSmallIcon(R.drawable.ic_action_dialog)
      .setContentIntent(pendingIntent)
      .setContentTitle(unReadMsg.size + " New Messages")
      .setContentText(newestMsg.getOtherPerson +": " + newestMsg.getText)
      .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
      .setLights(0xFF0000FF, 300,3000)  // blue light, 300ms on, 3s off
      .setAutoCancel(true)


    if (Build.VERSION.SDK_INT >= 16) {
      val inboxStyle = new Notification.InboxStyle()
        .setSummaryText("Touch to view friend list")

      // show at most the 5 newest unread messages
      unReadMsg.reverse.slice(0, 5).map((msg) => inboxStyle.addLine(msg.getOtherPerson+": "+msg.getText))

      builder.setStyle(inboxStyle)
      builder.setPriority(Notification.PRIORITY_MAX)
    }

    val notification = builder.build()
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(id, notification)
  }

  def onEvent(event: Events.ClearChatNotification): Unit = {
    info("[*]onEvent: clear chat notification")
    notificationManager.cancel(id)  // clear notification
  }
}
