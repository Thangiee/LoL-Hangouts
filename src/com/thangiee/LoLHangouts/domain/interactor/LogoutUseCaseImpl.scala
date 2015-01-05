package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.api.core.LoLChat

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class LogoutUseCaseImpl() extends LogoutUseCase {
  override def logout(): Unit = Future {
    LoLChat.disconnect()
    logoutListener.notify(Unit)
  }
}
