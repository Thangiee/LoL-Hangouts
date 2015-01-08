package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.UserRepo
import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ChangeUserStatusCaseImpl(implicit userRepo: UserRepo) extends ChangeUserStatusCase {

  override def appearOnline(): Future[Unit] = Future {
    info("[*] appearing online")
    userRepo.setAppearanceOnline().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def appearOffline(): Future[Unit] = Future {
    info("[*] appearing offline")
    userRepo.setAppearanceOffline().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def appearAway(): Future[Unit] = Future {
    info("[*] appearing away")
    userRepo.setAppearanceAway().map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }

  override def setStatusMsg(msg: String): Future[Unit] =Future {
    info(s"[*] setting status msg to: $msg")
    userRepo.setStatusMsg(msg).map(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    })
  }
}
