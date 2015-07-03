package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SetActiveChatUseCase extends Interactor {
  def setActiveChat(friend: Friend): Future[Unit]
}

case class SetActiveChatUseCaseImpl() extends SetActiveChatUseCase {

  override def setActiveChat(friend: Friend): Future[Unit] = Future {
    Cached.friendChat_=(Cached.loginUsername → friend.name)
    info(s"[+] active chat set to ${friend.name}")
  }
}
