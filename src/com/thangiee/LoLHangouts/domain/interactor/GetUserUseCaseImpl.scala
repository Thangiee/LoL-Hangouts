package com.thangiee.LoLHangouts.domain.interactor


import com.thangiee.LoLHangouts.domain.repository.UserRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetUserUseCaseImpl(implicit userRepo: UserRepo) extends GetUserUseCase {

  override def loadUser(): Unit = Future {
    userRepo.getActiveUser.fold(
      error => throw error,
      user  => loadUserListener.notify(user)
    )
  }
}
