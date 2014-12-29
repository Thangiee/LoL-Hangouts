package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Region
import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsLoginOffline
import com.thangiee.LoLHangouts.domain.entities.Value.String.{Password, Username}
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.utils.Listener

trait LoginUseCase extends Interactor {
  protected val loginListener = Listener[Unit]()
  protected val loadLoginInfoListener = Listener[(Username, Password, Option[Region], IsLoginOffline)]()
  protected val saveLoginInfoListener = Listener[Unit]()
  protected val errorListener = Listener[ErrorBundle]()

  def onLoadLoginInfo(listener: (Username, Password, Option[Region], IsLoginOffline) => Unit) = loadLoginInfoListener.addListener(listener.tupled)

  def onLogin(listener: Unit => Unit) = loginListener.addListener(listener)

  def onSaveLoginInfo(listener: Unit => Unit) = saveLoginInfoListener.addListener(listener)

  def onError(listener: ErrorBundle => Unit) = errorListener.addListener(listener)

  def loadLoginInfo(): Unit

  def login(user: String, pass: String)

  def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Unit
}
