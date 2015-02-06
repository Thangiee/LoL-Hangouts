package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Friend
import com.thangiee.lolhangouts.domain.repository.UserRepo
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class SetActiveChatUseCaseImpl(implicit userRepo: UserRepo) extends SetActiveChatUseCase {

  override def setActiveChat(friend: Friend): Future[Unit] = Future {
    userRepo.setFriendChat(friend.name).map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    }).getOrElse(info(s"[+] active chat set to ${friend.name}"))
  }
}
