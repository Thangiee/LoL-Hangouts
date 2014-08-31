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
    val summ = LoLChat.getFriendById(id).get
    val listener = LoLChat.friendListListener

    // notify when a friend login/logoff
    val previousType = typeSnapShot.get(id).get
    if (previousType == Type.unavailable && summ.chatType == Type.available) listener.onFriendLogin(summ) // login
    else if (previousType == Type.available && summ.chatType == Type.unavailable) listener.onFriendLogOff(summ) // logout

    // notify when chat mod of a friend change
    val previousMode = modeSnapShot.get(id).get
    if(previousMode != Mode.chat && summ.chatMode == Mode.chat) listener.onFriendAvailable(summ) // available (green)
    else if (previousMode != Mode.away && summ.chatMode == Mode.away) listener.onFriendAway(summ) // away (red)
    else if (previousMode != Mode.dnd && summ.chatMode == Mode.dnd ) listener.onFriendBusy(summ)  // busy (yellow)

    // notify when a friend chat status change
    val previousStatus = statusSnapShot.get(id).get
    if(previousStatus != summ.status) listener.onFriendStatusChange(summ)

    updateSnapShots(summ)
  }

  private def updateSnapShots(summoner: Summoner) {
    typeSnapShot.put(summoner.id, summoner.chatType)
    modeSnapShot.put(summoner.id, summoner.chatMode)
    statusSnapShot.put(summoner.id, summoner.status)
  }
}
