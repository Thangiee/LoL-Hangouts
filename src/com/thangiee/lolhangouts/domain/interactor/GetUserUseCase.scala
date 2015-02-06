package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.User

import scala.concurrent.Future

trait GetUserUseCase extends Interactor {

  def loadUser(): Future[User]
}
