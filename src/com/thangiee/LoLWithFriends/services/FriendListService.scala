package com.thangiee.LoLWithFriends.services

import android.app.{Notification, PendingIntent}
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import com.thangiee.LoLWithFriends.activities.MainActivity
import com.thangiee.LoLWithFriends.api.{FriendListListener, LoLChat, Summoner}
import com.thangiee.LoLWithFriends.utils.Events
import com.thangiee.LoLWithFriends.utils.Events.RefreshSummonerCard
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.scaloid.common._

import scala.util.Random

class FriendListService extends SService with FriendListListener {
  private val id = Random.nextInt()

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initFriendListListener(this)
  }

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

  private def showNotification(friend: Summoner) {
    // intent to bring the app to foreground
    val i = new Intent(ctx, classOf[MainActivity])
    val pendingIntent = PendingIntent.getActivity(ctx, 0, i, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

    val builder = new Notification.Builder(getApplicationContext)
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

    notificationManager.notify(id, notification)
  }
}
