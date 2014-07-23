package com.thangiee.LoLWithFriends.services

import android.content.Intent
import android.os.IBinder
import com.thangiee.LoLWithFriends.api.{LoLChat, FriendListListener, Summoner}
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import org.scaloid.common.{SService, UnregisterReceiver}

class FriendListService extends SService with UnregisterReceiver with FriendListListener {

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    LoLChat.initFriendListListener(this)
  }

  override def onFriendAvailable(summoner: Summoner): Unit = {}

  override def onFriendLogin(summoner: Summoner): Unit = {
    println("LOGIN")
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  override def onFriendBusy(summoner: Summoner): Unit = {}

  override def onFriendAway(summoner: Summoner): Unit = {}

  override def onFriendLogOff(summoner: Summoner): Unit = {
    println("LOGOUT")
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  override def onFriendStatusChange(summoner: Summoner): Unit = {}
}
