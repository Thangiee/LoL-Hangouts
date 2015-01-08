package com.thangiee.LoLHangouts.domain.interactor


import com.thangiee.LoLHangouts.domain.repository.UserRepo
import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetUserUseCaseImpl(implicit userRepo: UserRepo) extends GetUserUseCase {

  override def loadUser(): Unit = Future {
    userRepo.getActiveUser.left.map(e => { error(s"[!] ${e.getMessage}", e.getCause); throw e })
  }
}
