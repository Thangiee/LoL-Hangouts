package com.thangiee.lolhangouts.ui.services

import java.util.Date

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.graphics.Color
import android.media.{MediaPlayer, RingtoneManager}
import android.os.{Build, IBinder}
import com.thangiee.lolchat._
import com.thangiee.lolchat.changedPresence._
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.MessageEntity
import com.thangiee.lolhangouts.data.datasources.entities.mappers.{FriendMapper, MessageMapper}
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.ui.login.LoginActivity
import com.thangiee.lolhangouts.ui.main.MainActivity
import com.thangiee.lolhangouts.ui.utils.Events.{Logout => EventLogout, _}
import com.thangiee.lolhangouts.ui.utils._
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.Crouton
import org.scaloid.common.SService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class LoLHangoutsService extends SService with ReceiveMsgListener with FriendListListener with ReconnectionListener {
  private val msgNotificationId        = Random.nextInt()
  private val loginNotificationId      = Random.nextInt()
  private val disconnectNotificationId = Random.nextInt()
  private val runningNotificationId    = Random.nextInt()
  private val availableNotificationId  = Random.nextInt()

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    try {
      info("[*] Service started")
      LoLChat.findSession(Cached.loginUsername).map { sess =>
        sess.addReceiveMsgListener(this)
        sess.addReconnectionListener(this)
        sess.setFriendListListener(this)
      }
      EventBus.getDefault.registerSticky(ctx)
      if (isNotifyAppRunningPrefOn) notifyAppRunning()
    } catch {
      case e: IllegalStateException ⇒ warn("[!] " + e.getMessage); notifyDisconnection(); stopSelf()
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

  override def onReceivedMessage(fromId: String, textMsg: String): Unit = {
    for {
      sess <- LoLChat.findSession(Cached.loginUsername)
      from <- sess.findFriendById(fromId).map(FriendMapper.transform)
    } yield {
      val activeFriendChat = Cached.friendChat(Cached.loginUsername).getOrElse("")

      // create Message object with the received chat message
      val msgEntity = new MessageEntity(Cached.loginUsername, from.name, textMsg, false, true, new Date())

      // chat pane fragment is not open
      // or the current open chat is not with sender of the message
      if (!appCtx.isChatOpen || activeFriendChat != from.name || !isAppInForeground) {
        msgEntity.setRead(false) // set to false because user has not seen it
      }

      msgEntity.save() // save to DB
      EventBus.getDefault.post(Events.UpdateFriendCard(from.name))

      val msg = MessageMapper.transform(msgEntity)
      EventBus.getDefault.post(Events.IncomingMessage(from, msg))

      // check notification preference
      val isNotify = R.string.pref_notify_msg.pref2Boolean(default = true)

      // show notifications
      if (!isAppInForeground) {
        if (isNotify) {
          debug("[+] Notifying message")
          notifyMessage(msg)
        }
      } else if (!appCtx.isChatOpen || activeFriendChat != from.name) {
        if (isNotify) {
          debug("[+] Notifying message")
          notifyMessage(msg)
        }
      }

      // show nifty notifications
      if ((activeFriendChat != from.name || !appCtx.isChatOpen) && isAppInForeground) {
        niftyNotificationEventBus.post(Events.ShowNiftyNotification(msg))
      }
    }
  }

  def onFriendPresenceChanged(friend: FriendEntity)(changedPresence: ChangedPresence): Unit = {
    info(s"[*] Friend activity: ${friend.name} > $changedPresence")
    val f = FriendMapper.transform(friend)

    changedPresence match {
      case Login =>
        EventBus.getDefault.postSticky(ReloadFriendCardList())

        if (R.string.pref_notify_login.pref2Boolean(default = true)) {
          // show notification when friendList is not in view or screen is not on
          if (!appCtx.isFriendListOpen || !powerManager.isScreenOn || !isAppInForeground) {
            // check setting
            notifyLogin(f)
          }
        }

      case Logout =>
        EventBus.getDefault.postSticky(ReloadFriendCardList())
        appCtx.FriendsToNotifyOnAvailable.remove(f.name)

      case Available =>
        if (appCtx.FriendsToNotifyOnAvailable.remove(f.name)) notifyAvailable(f)
        EventBus.getDefault.post(UpdateFriendCard(f.name))

      case _ => EventBus.getDefault.post(UpdateFriendCard(f.name))
    }
  }

  def onReceivedFriendRequest(fromId: String): Boolean = true //todo: ask user if they want to accept

  def onFriendAdded(id: String): Unit = {
    EventBus.getDefault.post(ReloadFriendCardList())
    info("[+] New friend added")
  }

  def onFriendRemoved(id: String): Unit = {
    EventBus.getDefault.post(ReloadFriendCardList())
    info("[+] A friend was removed")
  }

  def onLostConnection(): Unit = {
    warn("[*] Connection lost")
    notificationManager.cancel(runningNotificationId)
    notifyDisconnection()
  }

  def onReconnected(): Unit = {
    info("[+] Reconnection successful")
    notificationManager.cancel(disconnectNotificationId)
    if (isNotifyAppRunningPrefOn) notifyAppRunning()
    EventBus.getDefault.post(ReloadFriendCardList())
    runOnUiThread(Crouton.cancelAllCroutons())
  }

  def onReconnectionFailed(attempt: Int): Unit = {
    info(s"[!] attempt $attempt to reconnect failed")
    // cancel reconnection after a number of attempts
    if (attempt > 6) {
      EventBus.getDefault.post(FinishActivity())
      EventBus.getDefault.post(Events.Logout())
      val i = new Intent(getBaseContext, classOf[LoginActivity])
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(i)
    }
  }

  def onReconnectingIn(sec: Int): Unit = info("[*] Connecting in " + sec)

  private def notifyLogin(friend: Friend) {
    val title = friend.name + " has logged in!"
    val content = "Touch to open application"
    val builder = makeNotificationBuilder(R.drawable.ic_action_user_yellow, title, content)

    if (isNotifySoundPrefOn) builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // set sound

    val notification = builder.getNotification
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(loginNotificationId, notification)
  }

  private def notifyAvailable(friend: Friend): Unit = {
    val title = friend.name + " is available."
    val content = "Touch to open application"
    val builder = makeNotificationBuilder(R.drawable.ic_action_user_green, title, content)

    if (isNotifySoundPrefOn) builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // set sound

    val notification = builder.getNotification
    notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(availableNotificationId, notification)
  }

  private def notifyMessage(newestMsg: com.thangiee.lolhangouts.data.usecases.entities.Message) {
    val unReadMsg = DB.getUnreadMessages(Cached.loginUsername, 5) // get the 5 newest unread messages
    val title = (if (unReadMsg.size >= 5) "+" else "") + unReadMsg.size + " New Messages"
    val content = newestMsg.friendName + ": " + newestMsg.text
    val builder = makeNotificationBuilder(R.drawable.ic_action_dialog, title, content)
    builder.setTicker(content)

    if (isNotifySoundPrefOn) // check setting
      MediaPlayer.create(ctx, R.raw.alert_pm_receive).start()

    if (Build.VERSION.SDK_INT >= 16) {
      val inboxStyle = new Notification.InboxStyle() // InboxStyle on available SDK greater than 16
        .setSummaryText("Touch to open application")

      unReadMsg.map((msg) => inboxStyle.addLine(msg.friendName + ": " + msg.text))
      builder.setStyle(inboxStyle)
      builder.setPriority(Notification.PRIORITY_MAX)
    }

    val notification = builder.getNotification
    if (R.string.pref_notify_vibrate.pref2Boolean(default = true)) // check setting
      notification.defaults |= Notification.DEFAULT_VIBRATE // enable vibration

    notificationManager.notify(msgNotificationId, notification)
  }

  private def notifyDisconnection(): Unit = {
    val i = new Intent(ctx, classOf[MainActivity])
    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    val p = PendingIntent.getActivity(ctx, 0, i, 0)
    val contentText = "Connection lost!"
    val builder = makeNotificationBuilder(R.drawable.ic_action_warning, R.string.app_name.r2String, contentText, Color.YELLOW)
    builder.setContentIntent(p)

    val notification = builder.getNotification
    notificationManager.notify(disconnectNotificationId, notification)
    EventBus.getDefault.post(Events.ShowDisconnection())
  }

  private def notifyAppRunning(): Unit = {
    val builder = new Notification.Builder(ctx)
      .setLargeIcon(R.drawable.ic_launcher.toBitmap)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentIntent(pendingActivity[MainActivity])
      .setContentTitle(Cached.loginUsername)
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
      .setLights(lightColor, 300, 3000) // blue light, 300ms on, 3s off
      .setAutoCancel(true)
  }

  private def isNotifySoundPrefOn: Boolean = R.string.pref_notify_sound.pref2Boolean(default = true)

  private def isNotifyAppRunningPrefOn: Boolean = R.string.pref_notify_app_running.pref2Boolean(default = true)

  def onEvent(event: Events.ClearChatNotification): Unit = {
    notificationManager.cancel(msgNotificationId) // clear notification
  }

  def onEvent(event: Events.ClearLoginNotification): Unit = {
    notificationManager.cancel(loginNotificationId) // clear notification
  }

  def onEvent(event: EventLogout): Unit = {
    info("[*] Cleaning up and disconnecting")
    Future {
      LoLChat.endAllSessions()
    } onSuccess { case () =>
      stopSelf()
    }
  }
}
