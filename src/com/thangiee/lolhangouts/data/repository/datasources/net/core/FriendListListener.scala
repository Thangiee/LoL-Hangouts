package com.thangiee.lolhangouts.data.repository.datasources.net.core

import com.thangiee.lolhangouts.data.entities.FriendEntity
import org.jivesoftware.smack.packet.Packet

trait FriendListListener {

  def onFriendRequest(address: String, summonerId: String, request: Packet)

  def onFriendAdded(id: String, name: String)

  def onFriendRemove(id: String, name: String)

  def onFriendAvailable(friend: FriendEntity)

  def onFriendAway(friend: FriendEntity)

  def onFriendBusy(friend: FriendEntity)

  def onFriendLogin(friend: FriendEntity)

  def onFriendLogOff(friend: FriendEntity)

  def onFriendStatusChange(friend: FriendEntity)
}
