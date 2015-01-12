package com.thangiee.LoLHangouts.ui.login

import com.thangiee.LoLHangouts.Presenter

trait LoginView {

  def presenter: Presenter

  def showProgress(): Unit

  def hideProgress(): Unit

  def showLoginSuccess(): Unit

  def showChangeLog(): Unit

  def showErrorMsg(msg: String): Unit

  def navigateToHome(): Unit

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

  def isLoginOffline: Boolean
}
