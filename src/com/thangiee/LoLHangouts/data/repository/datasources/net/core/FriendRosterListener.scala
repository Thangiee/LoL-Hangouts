package com.thangiee.LoLHangouts.data.repository.datasources.net.core

import java.util

import com.thangiee.LoLHangouts.api.utils.RiotApi
import org.jivesoftware.smack.RosterListener
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.{Mode, Type}
import org.jivesoftware.smack.util.StringUtils

import scala.collection.JavaConversions.collectionAsScalaIterable

class FriendRosterListener extends RosterListener {
  private var typeSnapShot   = Map[String, Type]()
  private var modeSnapShot   = Map[String, Mode]()
  private var statusSnapShot = Map[String, String]()
  LoLChat.friends.map((f) => updateSnapShots(f))

  override def entriesAdded(addresses: util.Collection[String]): Unit = {
    addresses.map(parseId).map{ id =>
      RiotApi.getSummonerName(id).map(name => LoLChat.friendListListener.onFriendAdded(id, name))
    }
  }

  override def entriesUpdated(addresses: util.Collection[String]): Unit = { }

  override def entriesDeleted(addresses: util.Collection[String]): Unit = {
    addresses.map(parseId).map{ id =>
      RiotApi.getSummonerName(id).map(name => LoLChat.friendListListener.onFriendRemove(id, name))
    }
  }

  override def presenceChanged(p: Presence): Unit = {
    val id = StringUtils.parseBareAddress(p.getFrom)
    val friend = LoLChat.getFriendById(id).get
    val listener = LoLChat.friendListListener

    // notify when a friend login/logoff
    val previousType = typeSnapShot.getOrElse(id, Type.unavailable)
    if (previousType == Type.unavailable && friend.chatType == Type.available) listener.onFriendLogin(friend) // login
    else if (previousType == Type.available && friend.chatType == Type.unavailable) listener.onFriendLogOff(friend) // logout

    // notify when chat mode of a friend change
    val previousMode = modeSnapShot.getOrElse(id, Mode.away)
    if (previousMode != Mode.chat && friend.chatMode == Mode.chat && previousType != Type.unavailable) listener.onFriendAvailable(friend) // available (green)
    else if (previousMode != Mode.away && friend.chatMode == Mode.away) listener.onFriendAway(friend) // away (red)
    else if (previousMode != Mode.dnd && friend.chatMode == Mode.dnd) listener.onFriendBusy(friend) // busy (yellow)

    // notify when a friend chat status change
    val previousStatus = statusSnapShot.getOrElse(id, "")
    if (!previousStatus.equals(friend.status)) listener.onFriendStatusChange(friend)

    updateSnapShots(friend)
  }

  private def updateSnapShots(friend: Friend) {
    typeSnapShot += friend.addr → friend.chatType
    modeSnapShot += friend.addr → friend.chatMode
    statusSnapShot += friend.addr → friend.status
  }

  private def parseId(address: String): String = "[0-9]+".r.findFirstIn(address).getOrElse("0")
}

