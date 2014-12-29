package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.Friend

trait FriendRepo {
  def getFriendList: List[Friend]

  def getFriendById(id: String): Friend

  def getFriendByName(name: String): Friend

  def setFriendListListener(): Unit

  def addFriend(id: String): Unit

  def removeFriend(id: String): Unit
}
