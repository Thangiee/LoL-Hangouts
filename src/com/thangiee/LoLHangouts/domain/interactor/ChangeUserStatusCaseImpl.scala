package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.UserRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ChangeUserStatusCaseImpl(implicit userRepo: UserRepo) extends ChangeUserStatusCase {
  override def appearOnline(): Unit = Future(userRepo.setAppearanceOnline().map(throw _))

  override def appearOffline(): Unit = Future(userRepo.setAppearanceOffline().map(throw _))

  override def appearAway(): Unit = Future(userRepo.setAppearanceAway()).map(throw _)

  override def setStatusMsg(msg: String): Unit = Future(userRepo.setStatusMsg(msg)).map(throw _)
}
