package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.User

trait GetUserUseCase extends SimpleInteractor[User] {
  def loadUser()
}
