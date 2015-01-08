package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.api.core.LoLChat

import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LogoutUseCaseImpl() extends LogoutUseCase {
  override def logout(): Future[Unit] = Future {
    info("[*] Logging out")
    LoLChat.disconnect()
  }
}
