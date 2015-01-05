package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.Friend

trait FriendRepo {
  def getFriendList: Either[Exception, List[Friend]]

  def getFriendById(id: String): Either[Exception, Friend]

  def getFriendByName(name: String): Either[Exception, Friend]

  def setFriendListListener(): Option[Exception]

  def addFriend(id: String): Option[Exception]

  def removeFriend(id: String): Option[Exception]
}
