package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.User
import com.thangiee.LoLHangouts.domain.utils.Listener

trait GetUserUseCase extends Interactor {
  protected val loadUserListener = Listener[User]()

  def onLoadUser(listener: User => Unit) = loadUserListener.addListener(listener)

  def loadUser(): Unit
}
