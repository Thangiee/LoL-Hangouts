package com.thangiee.LoLWithFriends.services

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import com.thangiee.LoLWithFriends.api.{LoLStatus, FriendListListener, LoLChat, Summoner}
import com.thangiee.LoLWithFriends.utils.Events
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.scaloid.common.{SService, SystemService, UnregisterReceiver}

class FriendListService extends SService with UnregisterReceiver with FriendListListener with SystemService {

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initFriendListListener(this)
  }

  override def onFriendAvailable(summoner: Summoner): Unit = {}

  override def onFriendLogin(summoner: Summoner): Unit = {
    println("LOGIN")
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)

    // show notification when friendList fragment is not in view
    if (!MyApp.isFriendListOpen) {
      val builder = new Notification.Builder(ctx)
        .setSmallIcon(R.drawable.mlv__default_avatar)
        .setContentText(summoner.name+" Login")

      val notification = builder.build()
      notificationManager.notify(1, notification)
    }
  }

  override def onFriendBusy(summoner: Summoner): Unit = {}

  override def onFriendAway(summoner: Summoner): Unit = {}

  override def onFriendLogOff(summoner: Summoner): Unit = {
    println("LOGOUT")
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  override def onFriendStatusChange(summoner: Summoner): Unit = {
    println(LoLStatus.get(summoner, LoLStatus.ProfileIcon))
  }
}
