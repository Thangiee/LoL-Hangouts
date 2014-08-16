package com.thangiee.LoLHangouts.api

trait FriendListListener {

  def onFriendAvailable(summoner: Summoner)

  def onFriendAway(summoner: Summoner)

  def onFriendBusy(summoner: Summoner)

  def onFriendLogin(summoner: Summoner)

  def onFriendLogOff(summoner: Summoner)

  def onFriendStatusChange(summoner: Summoner)
}
