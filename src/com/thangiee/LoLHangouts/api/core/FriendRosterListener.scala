package com.thangiee.LoLHangouts.api.core

import java.util

import org.jivesoftware.smack.RosterListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.{Mode, Type}
import org.jivesoftware.smack.util.StringUtils

import scala.collection.JavaConversions._

class FriendRosterListener extends RosterListener {
  private val typeSnapShot = scala.collection.mutable.HashMap[String, Type]()
  private val modeSnapShot = scala.collection.mutable.HashMap[String, Mode]()
  private val statusSnapShot = scala.collection.mutable.HashMap[String, String]()
  LoLChat.friends.map((f) => updateSnapShots(f))

  override def entriesAdded(summonerIds: util.Collection[String]): Unit = {
    summonerIds.map((id) => updateSnapShots(LoLChat.getFriendById(id).get))
  }

  override def entriesUpdated(summonerIds: util.Collection[String]): Unit = {}

  override def entriesDeleted(summonerIds: util.Collection[String]): Unit = {}

  override def presenceChanged(p: Presence): Unit = {
    val id = StringUtils.parseBareAddress(p.getFrom)
    val friend = LoLChat.getFriendById(id).get
    val listener = LoLChat.friendListListener

    // notify when a friend login/logoff
    val previousType = typeSnapShot.getOrElse(id, Type.unavailable)
    if (previousType == Type.unavailable && friend.chatType == Type.available) { listener.onFriendLogin(friend); return }// login
    else if (previousType == Type.available && friend.chatType == Type.unavailable) { listener.onFriendLogOff(friend); return }// logout

    // notify when chat mode of a friend change
    val previousMode = modeSnapShot.getOrElse(id, Mode.away)
    if(previousMode != Mode.chat && friend.chatMode == Mode.chat) listener.onFriendAvailable(friend) // available (green)
    else if (previousMode != Mode.away && friend.chatMode == Mode.away) listener.onFriendAway(friend) // away (red)
    else if (previousMode != Mode.dnd && friend.chatMode == Mode.dnd ) listener.onFriendBusy(friend)  // busy (yellow)

    // notify when a friend chat status change
    val previousStatus = statusSnapShot.getOrElse(id, "")
    if(!previousStatus.equals(friend.status)) listener.onFriendStatusChange(friend)

    updateSnapShots(friend)
  }

  private def updateSnapShots(friend: Friend) {
    typeSnapShot.put(friend.id, friend.chatType)
    modeSnapShot.put(friend.id, friend.chatMode)
    statusSnapShot.put(friend.id, friend.status)
  }
}

