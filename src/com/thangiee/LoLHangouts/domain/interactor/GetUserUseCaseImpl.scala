package com.thangiee.LoLHangouts.domain.interactor


import com.thangiee.LoLHangouts.domain.repository.UserRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GetUserUseCaseImpl(implicit userRepo: UserRepo) extends GetUserUseCase {

  override def loadUser(): Unit = Future {
    userRepo.getActiveUser.fold(
      error => errorListener.notify(error),
      user  => completeListener.notify(user)
    )
  }
}
