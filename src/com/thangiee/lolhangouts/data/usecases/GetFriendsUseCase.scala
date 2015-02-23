package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.entities.mappers.FriendMapper
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.exception.DataAccessException._
import com.thangiee.lolhangouts.data.exception.UseCaseException.InternalError
import com.thangiee.lolhangouts.data.exception.{DataAccessException, UseCaseException}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait GetFriendsUseCase extends Interactor {
  def loadOnlineFriends(): Future[List[Friend]]
  def loadFriendList(): Future[List[Friend]]
  def loadFriendByName(name: String): Future[Friend]
}

case class GetFriendsUseCaseImpl() extends GetFriendsUseCase {

  override def loadFriendList(): Future[List[Friend]] = Future {
    if (LoLChat.isLogin)
      LoLChat.friends.map(FriendMapper.transform).logThenReturn(_ => "[+] friend list loaded")
    else
      UseCaseException("[!] LoLChat is not login.", InternalError).logThenThrow.w
  }

  override def loadOnlineFriends(): Future[List[Friend]] = {
    loadFriendList().map(_.filter(_.isOnline))
  }

  override def loadFriendByName(name: String): Future[Friend] = Future {
    if (LoLChat.isLogin) {
      LoLChat.getFriendByName(name) match {
        case Some(friend) =>
          FriendMapper.transform(friend).logThenReturn(f => s"[+] friend loaded by name: ${f.name}")
        case None         =>
          DataAccessException(s"[-] Can't find $name in the friend list", DataNotFound).logThenThrow.i
      }
    } else {
      UseCaseException("LoLChat is not login", InternalError).logThenThrow.w
    }
  }
}