package com.thangiee.LoLHangouts.domain.interactor


import com.thangiee.LoLHangouts.domain.entities.User
import com.thangiee.LoLHangouts.domain.repository.UserRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetUserUseCaseImpl(implicit userRepo: UserRepo) extends GetUserUseCase {

  override def loadUser(): Future[User] = Future {
    userRepo.getActiveUser.fold(
      e => {
        error(s"[!] ${e.getMessage}", e.getCause)
        throw e
      },
      user => {
        info("[+] user loaded")
        user
      }
    )
  }
}
