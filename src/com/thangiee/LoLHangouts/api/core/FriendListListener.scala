package com.thangiee.LoLHangouts.api.core

import org.jivesoftware.smack.packet.Packet

trait FriendListListener {

  def onFriendRequest(address: String, summonerId: String, request: Packet)

  def onFriendAdded(id: String, name: String)

  def onFriendRemove(id: String, name: String)

  def onFriendAvailable(friend: Friend)

  def onFriendAway(friend: Friend)

  def onFriendBusy(friend: Friend)

  def onFriendLogin(friend: Friend)

  def onFriendLogOff(friend: Friend)

  def onFriendStatusChange(friend: Friend)
}
