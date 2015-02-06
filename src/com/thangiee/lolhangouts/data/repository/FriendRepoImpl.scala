package com.thangiee.lolhangouts.data.repository

import com.thangiee.lolhangouts.data.entities.mappers.FriendMapper
import com.thangiee.lolhangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.domain.entities.Friend
import com.thangiee.lolhangouts.domain.repository.FriendRepo
import thangiee.riotapi.core.RiotApi

trait FriendRepoImpl extends FriendRepo {
  implicit val caller = new CachingApiCaller()

  override def getFriendList: Either[Exception, List[Friend]] = {
    if (LoLChat.isLogin) {
      Right(LoLChat.friends.map(FriendMapper.transform))
    }
    else
      Left(new IllegalStateException("LoLChat is not login"))
  }

  override def addFriend(name: String): Option[Exception] = {
    RiotApi.summonerByName(name).fold(
      e => Some(e),
      summ => {
        LoLChat.connection.getRoster.createEntry(s"sum${summ.id}@pvp.net", name, null)
        None
      }
    )
  }

  override def removeFriend(id: String): Option[Exception] = {
    //todo: implement
    None
  }

  override def getOnlineFriend: Either[Exception, List[Friend]] = {
    getFriendList.right.map{ fl => fl.filter(_.isOnline) }
  }

  override def getFriendById(id: String): Either[Exception, Friend] = {
    if (LoLChat.isLogin) {
      LoLChat.getFriendById(id) match {
        case Some(friend) => Right(FriendMapper.transform(friend))
        case None         => Left(new IllegalArgumentException(s"Can't find anyone in the friend list with id: $id"))
      }
    } else {
      Left(new IllegalStateException("LoLChat is not login"))
    }
  }

  override def setFriendListListener(): Option[Exception] = {
    //todo: implement
    None
  }

  override def getFriendByName(name: String): Either[Exception, Friend] = {
    if (LoLChat.isLogin) {
      LoLChat.getFriendByName(name) match {
        case Some(friend) => Right(FriendMapper.transform(friend))
        case None         => Left(new IllegalArgumentException(s"Can't find $name in the friend list"))
      }
    } else {
      Left(new IllegalStateException("LoLChat is not login"))
    }
  }
}

object FriendRepoImpl extends FriendRepoImpl
