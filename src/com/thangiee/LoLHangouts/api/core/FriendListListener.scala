package com.thangiee.LoLHangouts.api.core

trait FriendListListener {

  def onFriendAvailable(friend: Friend)

  def onFriendAway(friend: Friend)

  def onFriendBusy(friend: Friend)

  def onFriendLogin(friend: Friend)

  def onFriendLogOff(friend: Friend)

  def onFriendStatusChange(friend: Friend)
}
