package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.{NoSession, NotFound}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.mappers.FriendMapper
import com.thangiee.lolhangouts.data.usecases.GetFriendsUseCase.{FriendNotFound, GetFriendError}
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.utils._
import org.scalactic._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GetFriendsUseCase extends Interactor {
  def loadOnlineFriends(): Future[Seq[Friend]]
  def loadFriendList(): Future[Seq[Friend]]
  def loadFriendByName(name: String): Future[Friend Or GetFriendError]
}

object GetFriendsUseCase {
  sealed trait GetFriendError
  object FriendNotFound extends GetFriendError
}

case class GetFriendsUseCaseImpl() extends GetFriendsUseCase {

  override def loadFriendList(): Future[Seq[Friend]] = Future {
    LoLChat.findSession(Cached.loginUsername) match {
      case Good(sess) => sess.friends.map(FriendMapper.transform).toSeq
      case Bad(NoSession(msg)) => warn(s"[!] $msg"); Seq.empty
    }
  }

  override def loadOnlineFriends(): Future[Seq[Friend]] = {
    loadFriendList().map(_.filter(_.isOnline))
  }

  override def loadFriendByName(name: String): Future[Friend Or GetFriendError] = Future {
    LoLChat.findSession(Cached.loginUsername).flatMap {
      _.findFriendByName(name).map(FriendMapper.transform)
    } badMap {
      case NoSession(msg) => warn(s"[!] $msg"); FriendNotFound
      case NotFound(msg) => info(s"[-] $msg"); FriendNotFound
    }
  }
}