package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.UserRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ChangeUserStatusCaseImpl(implicit userRepo: UserRepo) extends ChangeUserStatusCase {

  override def appearOnline(): Future[Unit] = Future {
    userRepo.setAppearanceOnline().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def appearOffline(): Future[Unit] = Future {
    userRepo.setAppearanceOffline().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def appearAway(): Future[Unit] = Future {
    userRepo.setAppearanceAway().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def setStatusMsg(msg: String): Future[Unit] =Future {
    userRepo.setStatusMsg(msg).map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }
}
