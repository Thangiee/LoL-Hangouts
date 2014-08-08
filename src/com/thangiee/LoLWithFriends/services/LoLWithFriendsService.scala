package com.thangiee.LoLWithFriends.services

import java.util.Date

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.media.{RingtoneManager, MediaPlayer}
import android.os.{Build, IBinder}
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.activities.MainActivity
import com.thangiee.LoLWithFriends.api.{Summoner, FriendListListener, LoLChat}
import com.thangiee.LoLWithFriends.utils.Events.{ClearLoginNotification, RefreshSummonerCard, ClearChatNotification}
import com.thangiee.LoLWithFriends.utils.{DataBaseHandler, Events}
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{ConnectionListener, Chat, MessageListener}
import org.scaloid.common._

import scala.util.Random

class LoLWithFriendsService extends SService with MessageListener with FriendListListener with ConnectionListener {
  private val msgNotificationId = Random.nextInt()
  private val loginNotificationId = Random.nextInt()

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initChatListener(this)
    LoLChat.initFriendListListener(this)
    LoLChat.connection.addConnectionListener(this)
    EventBus.getDefault.registerSticky(ctx)
  }

  override def onDestroy(): Unit = {
    notificationManager.cancelAll()
    EventBus.getDefault.unregister(this, classOf[ClearChatNotification], classOf[ClearLoginNotification])
    super.onDestroy()
  }

  //=============================================
  //    MessageListener Implementation
  //=============================================
  override def processMessage(chat: Chat, msg: Message): Unit = {
    val friend = LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get

    // create Message object with the received chat message
    val m = new models.Message.MessageBuilder(MESSAGE_TYPE_RECEIVED).text(msg.getBody).date(new Date())
      .otherPerson(friend.name).thisPerson(MyApp.currentUser).isRead(true).build()

    // chat pane fragment is not open
    // or the current open chat is not with sender of the message
    if (!MyApp.isChatOpen || MyApp.activeFriendChat != friend.name) {
      m.setIsRead(false) // set to false because user has not seen it
    }

    m.save() // save to DB
    EventBus.getDefault.post(new Events.RefreshSummonerCard(friend))

    // check notification preference
    val isNotify = defaultSharedPreferences.getBoolean(R.string.pref_notify_msg.r2String, true)

    if (MyApp.isChatOpen && MyApp.activeFriendChat == friend.name) {  // open & right -> post received msg
      EventBus.getDefault.post(new Events.ReceivedMessage(friend, m))
    } else if (!MyApp.isChatOpen && MyApp.activeFriendChat == friend.name){ // close & right -> post received msg and notification
      EventBus.getDefault.post(new Events.ReceivedMessage(friend, m))
      if (isNotify) showNotification(m)
    } else {                                                          // wrong friend -> post notification
      if (isNotify) showNotification(m)
    }
  }

  //=============================================
  //    FriendListListener Implementations
  //=============================================
  override def onFriendAvailable(summoner: Summoner): Unit = {
    info("[*]Available: "+summoner.name)
    EventBus.getDefault.post(new RefreshSummonerCard(summoner))
  }

  override def onFriendLogin(summoner: Summoner): Unit = {
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)

    if (defaultSharedPreferences.getBoolean(R.string.pref_notify_login.r2String, true)) {
      // show notification when friendList fragment is not in view or screen is not on
      if (!MyApp.isFriendListOpen || !powerManager.isScreenOn) { // check setting
        showNotification(summoner)
      }
    }
  }

  override def onFriendBusy(summoner: Summoner): Unit = {
    info("[*]Busy: "+summoner.name)
    EventBus.getDefault.post(new RefreshSummonerCard(summoner))
  }

  override def onFriendAway(summoner: Summoner): Unit = {
    info("[*]Away: "+summoner.name)
    EventBus.getDefault.post(new RefreshSummonerCard(summoner))
  }

  override def onFriendLogOff(summoner: Summoner): Unit = {
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  override def onFriendStatusChange(summoner: Summoner): Unit = {
    info("[*]Change Status: "+summoner.name)
    println(summoner.status)
  }

  //=============================================
  //    ConnectionListener Implementations
  //=============================================
  override def connectionClosed(): Unit = { info("[*] Connection closed.") }

  override def reconnectionFailed(p1: Exception): Unit = { warn("[!] Reconnection failed") }

  override def reconnectionSuccessful(): Unit = { info("[*] Reconnection successful") }

  override def connectionClosedOnError(p1: Exception): Unit = { warn("[!] Connection lost")}

  override def reconnectingIn(sec: Int): Unit = { info("Connecting in " + sec)}

  //=============================================

  private def showNotification(friend: Summoner) {
    // intent to bring the app to foreground
    val i = new Intent(ctx, classOf[MainActivity])
    val pendingIntent = PendingIntent.getActivity(ctx, 0, i, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

    val builder = new Notification.Builder(ctx)
      .setSmallIcon(R.drawable.ic_action_user)
      .setContentIntent(pendingIntent)
      .setContentTitle(friend.name + " has logged in!")
      .setContentText("Touch to view friend list")
      .setLights(0xFF0000FF, 300,3000)  // blue light, 300ms on, 3s off
      .setAutoCancel(true)
    if (defaultSharedPreferences.getBoolean(R.string.pref_notify_sound.r2String, true))
      builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // set sound

    val notification = builder.build()
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(loginNotificationId, notification)
  }

  private def showNotification(newestMsg: models.Message) {
    val unReadMsg = DataBaseHandler.getUnReadMessages

    // intent to bring the app to foreground
    val i = new Intent(ctx, classOf[MainActivity])
    val pendingIntent = PendingIntent.getActivity(ctx, 0, i, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

    val builder = new Notification.Builder(ctx)
      .setSmallIcon(R.drawable.ic_action_dialog)
      .setContentIntent(pendingIntent)
      .setContentTitle(unReadMsg.size + " New Messages")
      .setContentText(newestMsg.getOtherPerson +": " + newestMsg.getText)
      .setLights(0xFF0000FF, 300,3000)  // blue light, 300ms on, 3s off
      .setAutoCancel(true)
    if (defaultSharedPreferences.getBoolean(R.string.pref_notify_sound.r2String, true)) // check setting
      MediaPlayer.create(ctx, R.raw.alert_pm_receive).start()

    if (Build.VERSION.SDK_INT >= 16) {
      val inboxStyle = new Notification.InboxStyle() // InboxStyle on available SDK greater than 16
        .setSummaryText("Touch to view friend list")

      // show at most the 5 newest unread messages
      unReadMsg.reverse.slice(0, 5).map((msg) => inboxStyle.addLine(msg.getOtherPerson+": "+msg.getText))

      builder.setStyle(inboxStyle)
      builder.setPriority(Notification.PRIORITY_MAX)
    }

    val notification = builder.build()
    if (defaultSharedPreferences.getBoolean(R.string.pref_notify_vibrate.r2String, true)) // check setting
      notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(msgNotificationId, notification)
  }

  def onEvent(event: Events.ClearChatNotification): Unit = {
    info("[*]onEvent: clear chat notification")
    notificationManager.cancel(msgNotificationId)  // clear notification
  }

  def onEvent(event: Events.ClearLoginNotification): Unit = {
    info("[*]onEvent: clear login notification")
    notificationManager.cancel(loginNotificationId)  // clear notification
  }

}