package com.thangiee.LoLHangouts.domain.interactor


import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LogoutUseCaseImpl() extends LogoutUseCase {
  override def logout(): Future[Unit] = Future {
    LoLChat.disconnect()
  }
}
