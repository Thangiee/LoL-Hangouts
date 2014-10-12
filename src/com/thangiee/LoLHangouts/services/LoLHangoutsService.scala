package com.thangiee.LoLHangouts.services

import java.util.Date

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.graphics.Color
import android.media.{MediaPlayer, RingtoneManager}
import android.os.{Build, IBinder}
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{LoginActivity, MainActivity}
import com.thangiee.LoLHangouts.api.core.{Friend, FriendListListener, LoLChat}
import com.thangiee.LoLHangouts.api.utils.RiotApi
import com.thangiee.LoLHangouts.utils.Events._
import com.thangiee.LoLHangouts.utils.{DB, Events, TContext, TLogger}
import de.greenrobot.event.EventBus
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack.{Chat, ConnectionListener, MessageListener}
import org.scaloid.common._

import scala.util.Random

class LoLHangoutsService extends SService with TContext with MessageListener with FriendListListener with ConnectionListener with TLogger {
  private val msgNotificationId = Random.nextInt()
  private val loginNotificationId = Random.nextInt()
  private val disconnectNotificationId = Random.nextInt()
  private val runningNotificationId = Random.nextInt()
  private val availableNotificationId = Random.nextInt()

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    try {
      info("[*] Service started")
      LoLChat.initChatListener(this)
      LoLChat.initFriendListListener(this)
      LoLChat.connection.addConnectionListener(this)
      EventBus.getDefault.registerSticky(ctx)
      if (R.string.pref_notify_app_running.pref2Boolean(default = true)) notifyAppRunning()
    } catch {
      case e: IllegalStateException ⇒ error("[!] " + e.getMessage); notifyDisconnection(); stopSelf()
    }
  }

  override def onDestroy(): Unit = {
    notificationManager.cancel(msgNotificationId)
    notificationManager.cancel(loginNotificationId)
    notificationManager.cancel(runningNotificationId)
    notificationManager.cancel(availableNotificationId)
    EventBus.getDefault.unregister(this)
    info("[*] Service stop")
    super.onDestroy()
  }

  //=============================================
  //    MessageListener Implementation
  //=============================================
  override def processMessage(chat: Chat, msg: Message): Unit = {
    val friend = LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get

    // create Message object with the received chat message
    val m = new models.Message.MessageBuilder(MESSAGE_TYPE_RECEIVED).text(msg.getBody).date(new Date())
      .otherPerson(friend.name).thisPerson(appCtx.currentUser).isRead(true).build()

    // chat pane fragment is not open
    // or the current open chat is not with sender of the message
    if (!appCtx.isChatOpen || appCtx.activeFriendChat != friend.name) {
      m.setRead(false) // set to false because user has not seen it
    }

    m.save() // save to DB
    EventBus.getDefault.post(Events.RefreshFriendList())

    // check notification preference
    val isNotify = R.string.pref_notify_msg.pref2Boolean(default = true)

    if (appCtx.isChatOpen && appCtx.activeFriendChat == friend.name) {  // open & right -> post received msg
      EventBus.getDefault.post(Events.ReceivedMessage(friend, m))
    } else if (!appCtx.isChatOpen && appCtx.activeFriendChat == friend.name) { // close & right -> post received msg and notification
      EventBus.getDefault.post(Events.ReceivedMessage(friend, m))
      if (isNotify) notifyMessage(m)
    } else if (appCtx.isChatOpen && appCtx.activeFriendChat != friend.name) {
      EventBus.getDefault.post(Events.ShowNiftyNotification(m))     // open & wrong
      if (isNotify) notifyMessage(m)
    } else {
      if (isNotify) notifyMessage(m)                              // close & wrong -> post notification
    }
  }

  //=============================================
  //    FriendListListener Implementations
  //=============================================

  override def onFriendRequest(address: String, summonerId: String): Unit = {
    RiotApi.getSummonerName(summonerId) match {
      case Some(name) => LoLChat.connection.getRoster.createEntry(address, name, null) // auto accept request
      case None => error("[!] Unable to find summoner name")
    }
  }

  override def onFriendAdded(id: String, name: String): Unit = {
    croutonEventBus.post(CroutonMsg(s"$name has been added to your friend list"))
  }

  override def onFriendRemove(id: String, name: String): Unit = {
    EventBus.getDefault.post(RefreshFriendList())
    croutonEventBus.post(CroutonMsg(s"$name has been removed from your friend list"))
  }

  override def onFriendAvailable(friend: Friend): Unit = {
    info("[*]Available: "+friend.name)
    if (appCtx.FriendsToNotifyOnAvailable.remove(friend.name)) notifyAvailable(friend)
    EventBus.getDefault.post(RefreshFriendList())
  }

  override def onFriendLogin(friend: Friend): Unit = {
    EventBus.getDefault.postSticky(RefreshFriendList())

    if (R.string.pref_notify_login.pref2Boolean(default = true)) {
      // show notification when friendList fragment is not in view or screen is not on
      if (!appCtx.isFriendListOpen || !powerManager.isScreenOn) { // check setting
        notifyLogin(friend)
      }
    }
  }

  override def onFriendBusy(friend: Friend): Unit = {
    info("[*]Busy: "+friend.name)
    EventBus.getDefault.post(RefreshFriendList())
  }

  override def onFriendAway(friend: Friend): Unit = {
    info("[*]Away: "+friend.name)
    EventBus.getDefault.post(RefreshFriendList())
  }

  override def onFriendLogOff(friend: Friend): Unit = {
    EventBus.getDefault.postSticky(RefreshFriendList())
    appCtx.FriendsToNotifyOnAvailable.remove(friend.name)
  }

  override def onFriendStatusChange(friend: Friend): Unit = {
    info("[*]Change Status: "+friend.name)
    EventBus.getDefault.post(RefreshFriendList())
  }

  //=============================================
  //    ConnectionListener Implementations
  //=============================================
  override def connectionClosed(): Unit = { info("[*] Connection closed."); notificationManager.cancel(runningNotificationId) }

  override def reconnectionFailed(p1: Exception): Unit = { warn("[!] Reconnection failed") }

  override def reconnectionSuccessful(): Unit = { info("[*] Reconnection successful") }

  override def connectionClosedOnError(p1: Exception): Unit = {
    warn("[!] Connection lost")
    notificationManager.cancel(runningNotificationId)
    notifyDisconnection()
    stopSelf()
  }

  override def reconnectingIn(sec: Int): Unit = { info("Connecting in " + sec)}

  //=============================================

  private def notifyLogin(friend: Friend) {
    val title = friend.name + " has logged in!"
    val content = "Touch to open application"
    val builder = makeNotificationBuilder(R.drawable.ic_action_user_yellow, title, content)

    if (R.string.pref_notify_sound.pref2Boolean(default = true))
      builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // set sound

    val notification = builder.getNotification
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(loginNotificationId, notification)
  }

  private def notifyAvailable(friend: Friend): Unit = {
    val title = friend.name + " is available."
    val content = "Touch to open application"
    val builder = makeNotificationBuilder(R.drawable.ic_action_user_green, title, content)

    if (R.string.pref_notify_sound.pref2Boolean(default = true))
      builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // set sound

    val notification = builder.getNotification
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(availableNotificationId, notification)
  }

  private def notifyMessage(newestMsg: models.Message) {
    val unReadMsg = DB.getUnreadMessages(appCtx.currentUser, 5) // get the 5 newest unread messages
    val title = (if (unReadMsg.size >= 5) "+" else "") + unReadMsg.size + " New Messages"
    val content = newestMsg.getOtherPerson +": " + newestMsg.getText
    val builder = makeNotificationBuilder(R.drawable.ic_action_dialog, title, content)
    builder.setTicker(content)

    if (R.string.pref_notify_sound.pref2Boolean(default = true)) // check setting
      MediaPlayer.create(ctx, R.raw.alert_pm_receive).start()

    if (Build.VERSION.SDK_INT >= 16) {
      val inboxStyle = new Notification.InboxStyle() // InboxStyle on available SDK greater than 16
        .setSummaryText("Touch to open application")

      unReadMsg.map((msg) => inboxStyle.addLine(msg.getOtherPerson+": "+msg.getText))
      builder.setStyle(inboxStyle)
      builder.setPriority(Notification.PRIORITY_MAX)
    }

    val notification = builder.getNotification
    if (R.string.pref_notify_vibrate.pref2Boolean(default = true)) // check setting
      notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(msgNotificationId, notification)
  }

  private def notifyDisconnection(): Unit = {
    EventBus.getDefault.post(Events.FinishMainActivity()) // kill the main activity
    val i = new Intent(ctx, classOf[LoginActivity])
    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    val p = PendingIntent.getActivity(ctx, 0, i, 0)
    val contentText = "Connection lost. Touch to log in again."
    val builder = makeNotificationBuilder(R.drawable.ic_action_warning, R.string.app_name.r2String, contentText, Color.YELLOW)
    builder.setContentIntent(p)

    if (R.string.pref_notify_sound.pref2Boolean(default = true))
      builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // set sound

    val notification = builder.getNotification
    if (R.string.pref_notify_vibrate.pref2Boolean(default = true)) // check setting
      notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(disconnectNotificationId, notification)
  }

  private def notifyAppRunning(): Unit = {
    val builder = new Notification.Builder(ctx)
      .setLargeIcon(R.drawable.ic_launcher.toBitmap)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentIntent(pendingActivity[MainActivity])
      .setContentTitle(appCtx.currentUser)
      .setContentText("LoL Hangouts is running")
      .setOngoing(true)

    notificationManager.notify(runningNotificationId, builder.getNotification)
  }

  private def makeNotificationBuilder(icon: Int, title: String, content: String, lightColor: Int = Color.BLUE): Notification.Builder = {
    new Notification.Builder(ctx)
      .setLargeIcon(R.drawable.ic_launcher.toBitmap)
      .setSmallIcon(icon)
      .setContentIntent(pendingActivity[MainActivity])
      .setContentTitle(title)
      .setTicker(title)
      .setContentText(content)
      .setLights(lightColor, 300,3000)  // blue light, 300ms on, 3s off
      .setAutoCancel(true)
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
