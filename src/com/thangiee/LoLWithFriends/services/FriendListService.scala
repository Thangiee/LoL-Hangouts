package com.thangiee.LoLWithFriends.services

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import com.thangiee.LoLWithFriends.api.{FriendListListener, LoLChat, Summoner}
import com.thangiee.LoLWithFriends.utils.Events
import com.thangiee.LoLWithFriends.utils.Events.SummonerCardUpdated
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.scaloid.common.{SService, SystemService, UnregisterReceiver}

class FriendListService extends SService with UnregisterReceiver with FriendListListener with SystemService {

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initFriendListListener(this)
  }

  override def onFriendAvailable(summoner: Summoner): Unit = {
    EventBus.getDefault.post(new SummonerCardUpdated(summoner))
  }

  override def onFriendLogin(summoner: Summoner): Unit = {
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)

    // show notification when friendList fragment is not in view or screen is not on
    if (!MyApp.isFriendListOpen || !powerManager.isScreenOn) {
      val builder = new Notification.Builder(ctx)
        .setSmallIcon(R.drawable.mlv__default_avatar)
        .setContentText(summoner.name+" Login")

      val notification = builder.build()
      notificationManager.notify(1, notification)
    }
  }

  override def onFriendBusy(summoner: Summoner): Unit = {
    EventBus.getDefault.post(new SummonerCardUpdated(summoner))
  }

  override def onFriendAway(summoner: Summoner): Unit = {
    EventBus.getDefault.post(new SummonerCardUpdated(summoner))
  }

  override def onFriendLogOff(summoner: Summoner): Unit = {
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  override def onFriendStatusChange(summoner: Summoner): Unit = {
    println(summoner.status)
  }
}
