package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Region
import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsLoginOffline
import com.thangiee.LoLHangouts.domain.entities.Value.String.{ErrorMsg, Password, Username}
import com.thangiee.LoLHangouts.domain.utils.Listener

trait LoginUseCase extends Interactor {
  protected val loginListener              = Listener[Unit]()
  protected val loadLoginInfoListener      = Listener[(Username, Password, Option[Region], IsLoginOffline)]()
  protected val saveLoginInfoListener      = Listener[Unit]()
  protected val authorizationErrorListener = Listener[ErrorMsg]()
  protected val connectionErrorListener    = Listener[ErrorMsg]()
  protected val blankUsernameErrorListener = Listener[ErrorMsg]()
  protected val blankPasswordErrorListener = Listener[ErrorMsg]()

  def loadLoginInfo(): Unit

  def login(user: String, pass: String)

  def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Unit

  def onBlankPasswordError(listener: ErrorMsg => Unit) = blankPasswordErrorListener.addListener(listener)

  def onBlankUsernameError(listener: ErrorMsg => Unit) = blankUsernameErrorListener.addListener(listener)

  def onConnectionError(listener: ErrorMsg => Unit) = connectionErrorListener.addListener(listener)

  def onAuthorizationError(listener: ErrorMsg => Unit) = authorizationErrorListener.addListener(listener)

  def onLoadLoginInfo(listener: (Username, Password, Option[Region], IsLoginOffline) => Unit) = loadLoginInfoListener.addListener(listener.tupled)

  def onLogin(listener: Unit => Unit) = loginListener.addListener(listener)

  def onSaveLoginInfo(listener: Unit => Unit) = saveLoginInfoListener.addListener(listener)
}
