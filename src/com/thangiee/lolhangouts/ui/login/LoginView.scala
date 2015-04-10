package com.thangiee.lolhangouts.ui.login

import com.thangiee.lolhangouts.ui.core.Presenter

trait LoginView {

  protected def presenter: Presenter

  def setLoginState(state: Int): Unit

  def setGuessLoginState(state: Int): Unit

  def showChangeLog(): Unit

  def showBlankUsernameError(): Unit
  
  def showBlankPasswordError(): Unit
  
  def showConnectionError(): Unit
  
  def showAuthenticationError(): Unit

  def navigateToHome(isGuestMode: Boolean): Unit

  def navigateBack(): Unit

  def setTitle(title: String): Unit

  def setPassword(password: String): Unit

  def setUsername(name: String): Unit

  def getPassword: String

  def getUsername: String

  def getCurrentAppVersion: String

  def showLoginOffline(isEnable: Boolean): Unit
  
  def showSaveUsername(isEnable: Boolean): Unit
  
  def showSavePassword(isEnable: Boolean): Unit

  def showUpdateApp(version: String): Unit

  def isLoginOffline: Boolean
}

object LoginView {
  lazy val NormalState = 0
  lazy val LoadingState = 50
  lazy val SuccessState = 100
  lazy val ErrorState = -1
}
