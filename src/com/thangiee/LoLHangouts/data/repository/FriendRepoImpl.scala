package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.entities.mappers.FriendMapper
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.domain.repository.FriendRepo

case class FriendRepoImpl() extends FriendRepo {

  override def getFriendList: Either[Exception, List[Friend]] = {
    if (LoLChat.isLogin) {
      Right(LoLChat.friends.map(FriendMapper.transform))
    }
    else
      Left(new IllegalStateException("LoLChat is not login"))
  }

  override def addFriend(id: String): Option[Exception] = {
    //todo: implement
    None
  }

  override def removeFriend(id: String): Option[Exception] = {
    //todo: implement
    None
  }

  override def getOnlineFriend: Either[Exception, List[Friend]] = {
    getFriendList.right.map{ fl => fl.filter(_.isOnline) }
  }

  override def getFriendById(id: String): Either[Exception, Friend] = {
    if (LoLChat.isLogin)
      Right(FriendMapper.transform(LoLChat.getFriendById(id).get))
    else
      Left(new IllegalStateException("LoLChat is not login"))
  }

  override def setFriendListListener(): Option[Exception] = {
    //todo: implement
    None
  }

  override def getFriendByName(name: String): Either[Exception, Friend] = {
    if (LoLChat.isLogin)
      Right(FriendMapper.transform(LoLChat.getFriendByName(name).get))
    else
      Left(new IllegalStateException("LoLChat is not login"))
  }
}
