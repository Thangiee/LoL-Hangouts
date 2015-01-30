package com.thangiee.LoLHangouts.services

import java.util.Date

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.graphics.Color
import android.media.{MediaPlayer, RingtoneManager}
import android.os.{Build, IBinder}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.{FriendMapper, MessageMapper}
import com.thangiee.LoLHangouts.data.entities.{FriendEntity, MessageEntity}
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.{FriendListListener, LoLChat}
import com.thangiee.LoLHangouts.data.repository.datasources.sqlite.DB
import com.thangiee.LoLHangouts.domain.entities
import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.ui.main.MainActivity
import com.thangiee.LoLHangouts.utils.Events._
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.jivesoftware.smack.packet.{Message => XMPPMessage, Packet, Presence}
import org.jivesoftware.smack.util.StringUtils
import org.jivesoftware.smack._
import org.scaloid.common.SService
import thangiee.riotapi.core.RiotApi

import scala.util.Random

class LoLHangoutsService extends SService with MessageListener with FriendListListener with ConnectionListener {
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
      LoLChat.initChatListener(this)
      LoLChat.initFriendListListener(this)
      LoLChat.connection.addConnectionListener(this)
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

  //=============================================
  //    MessageListener Implementation
  //=============================================
  override def processMessage(chat: Chat, m: XMPPMessage): Unit = {
    val activeFriendChat = PrefsCache.getString(CacheKey.friendChat(LoLChat.loginName())).getOrElse("")
    val from = FriendMapper.transform(LoLChat.getFriendById(StringUtils.parseBareAddress(chat.getParticipant)).get)

    // create Message object with the received chat message
    val msgEntity = new MessageEntity(LoLChat.loginName(), from.name, m.getBody, false, true, new Date())

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

  //=============================================
  //    FriendListListener Implementations
  //=============================================

  override def onFriendRequest(address: String, summonerId: String, request: Packet): Unit = {
    implicit val apiCaller = new CachingApiCaller
    RiotApi.summonerNameById(summonerId.toLong).fold(
      error => warn(s"[!] Unable to find summoner name: ${error.msg}"),
      name => {
        LoLChat.connection.getRoster.createEntry(address, name, null) // add to friend list
        // notify sender of approved friend request
        val subscribed = new Presence(Presence.Type.subscribed)
        subscribed.setTo(request.getFrom)
        LoLChat.connection.sendPacket(subscribed)
      }
    )
  }

  override def onFriendAdded(id: String, name: String): Unit = {
    EventBus.getDefault.post(ReloadFriendCardList())
    croutonEventBus.post(CroutonMsg(s"Friend request has be sent to $name"))
  }

  override def onFriendRemove(id: String, name: String): Unit = {
    EventBus.getDefault.post(ReloadFriendCardList())
    croutonEventBus.post(CroutonMsg(s"$name has been removed from your friend list", Style.ALERT))
  }

  override def onFriendAvailable(friend: FriendEntity): Unit = {
    val f = FriendMapper.transform(friend)
    info("[*] Available: " + f.name)
    if (appCtx.FriendsToNotifyOnAvailable.remove(friend.name)) notifyAvailable(f)
    EventBus.getDefault.post(UpdateFriendCard(f.name))
  }

  override def onFriendLogin(friend: FriendEntity): Unit = {
    val f = FriendMapper.transform(friend)
    EventBus.getDefault.postSticky(ReloadFriendCardList())

    if (R.string.pref_notify_login.pref2Boolean(default = true)) {
      // show notification when friendList is not in view or screen is not on
      if (!appCtx.isFriendListOpen || !powerManager.isScreenOn || !isAppInForeground) {
        // check setting
        notifyLogin(f)
      }
    }
  }

  override def onFriendBusy(friend: FriendEntity): Unit = {
    info("[*] Busy: " + friend.name)
    EventBus.getDefault.post(UpdateFriendCard(friend.name))
  }

  override def onFriendAway(friend: FriendEntity): Unit = {
    info("[*] Away: " + friend.name)
    EventBus.getDefault.post(UpdateFriendCard(friend.name))
  }

  override def onFriendLogOff(friend: FriendEntity): Unit = {
    EventBus.getDefault.postSticky(ReloadFriendCardList())
    appCtx.FriendsToNotifyOnAvailable.remove(friend.name)
  }

  override def onFriendStatusChange(friend: FriendEntity): Unit = {
    info("[*] Change Status: " + friend.name)
    EventBus.getDefault.post(UpdateFriendCard(friend.name))
  }

  //=============================================
  //    ConnectionListener Implementations
  //=============================================
  override def connected(xmppConnection: XMPPConnection): Unit = {
    info("[*] connected")
  }

  override def authenticated(xmppConnection: XMPPConnection): Unit = {
    info("[*] authenticated")
  }

  override def connectionClosed(): Unit = {
    info("[*] Connection closed.")
    notificationManager.cancel(runningNotificationId)
  }

  override def reconnectionFailed(p1: Exception): Unit = {
    warn("[-] Reconnection failed")
  }

  override def reconnectionSuccessful(): Unit = {
    info("[*] Reconnection successful")
    notificationManager.cancel(disconnectNotificationId)
    if (isNotifyAppRunningPrefOn) notifyAppRunning()
    EventBus.getDefault.post(ReloadFriendCardList())
    runOnUiThread(Crouton.cancelAllCroutons())
  }

  override def connectionClosedOnError(e: Exception): Unit = {
    warn("[!] Connection lost:" + e.getMessage)
    notificationManager.cancel(runningNotificationId)
    notifyDisconnection()
  }

  override def reconnectingIn(sec: Int): Unit = {
    info("[*] Connecting in " + sec)
  }

  //=============================================

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

  private def notifyMessage(newestMsg: entities.Message) {
    val unReadMsg = DB.getUnreadMessages(LoLChat.loginName(), 5) // get the 5 newest unread messages
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
      .setContentTitle(LoLChat.loginName())
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
}
