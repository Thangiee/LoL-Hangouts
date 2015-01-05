package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.utils.Listener

trait LogoutUseCase extends Interactor {
  protected val logoutListener = Listener[Unit]()

  def onLogout(listener: Unit => Unit) = logoutListener.addListener(listener)

  def logout(): Unit
}
