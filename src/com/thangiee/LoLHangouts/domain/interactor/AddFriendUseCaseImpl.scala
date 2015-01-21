package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.FriendRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class AddFriendUseCaseImpl(implicit friendRepo: FriendRepo) extends AddFriendUseCase {

  override def addFriend(name: String): Future[Unit] = Future {
    friendRepo.addFriend(name) match {
      case Some(err) => error(s"[!] ${err.getMessage}", err.getCause); throw err
      case None      => info(s"[+] friend request sent to $name")
    }
  }
}
