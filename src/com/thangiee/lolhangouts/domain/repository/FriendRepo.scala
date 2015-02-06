package com.thangiee.lolhangouts.domain.repository

import com.thangiee.lolhangouts.domain.entities.Friend

trait FriendRepo {
  def getFriendList: Either[Exception, List[Friend]]

  def getOnlineFriend: Either[Exception, List[Friend]]

  def getFriendById(id: String): Either[Exception, Friend]

  def getFriendByName(name: String): Either[Exception, Friend]

  def setFriendListListener(): Option[Exception]

  def addFriend(name: String): Option[Exception]

  def removeFriend(name: String): Option[Exception]
}
