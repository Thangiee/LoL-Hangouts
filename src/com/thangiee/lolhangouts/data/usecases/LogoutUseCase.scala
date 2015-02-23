package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait LogoutUseCase extends Interactor {
  def logout(): Future[Unit]
}

case class LogoutUseCaseImpl() extends LogoutUseCase {
  override def logout(): Future[Unit] = Future {
    LoLChat.disconnect()
  }
}
