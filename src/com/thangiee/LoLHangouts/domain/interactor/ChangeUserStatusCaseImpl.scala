package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.UserRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ChangeUserStatusCaseImpl(implicit userRepo: UserRepo) extends ChangeUserStatusCase {
  override def appearOnline(): Unit = Future(userRepo.setAppearanceOnline().map(e => throw e))

  override def appearOffline(): Unit = Future(userRepo.setAppearanceOffline().map(e => throw e))

  override def appearAway(): Unit = Future(userRepo.setAppearanceAway().map(e => throw e))

  override def setStatusMsg(msg: String): Unit = Future(userRepo.setStatusMsg(msg).map(e => throw e))
}
