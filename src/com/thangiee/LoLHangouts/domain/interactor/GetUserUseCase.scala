package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.User

import scala.concurrent.Future

trait GetUserUseCase extends Interactor {

  def loadUser(): Future[User]
}
