package com.thangiee.lolhangouts.domain.interactor


import com.thangiee.lolhangouts.domain.entities.User
import com.thangiee.lolhangouts.domain.repository.UserRepo
import com.thangiee.lolhangouts.domain.utils._

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
