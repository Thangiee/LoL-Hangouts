package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.User

trait UserRepo {

  /**
   * try to login a user and set it as the active user
   *
   * @param username
   * @param password
   * @return
   */
  def loginUser(username: String, password: String): Option[Exception]

  /**
   * log out, if any, the currently logged in user
   */
  def logoutUser(): Option[Exception]

  /**
   * @return the user that is currently login or the user that is most recently saved
   */
  def getActiveUser: Either[Exception, User]

  def setAppearanceOnline(): Option[Exception]

  def setAppearanceAway(): Option[Exception]

  def setAppearanceOffline(): Option[Exception]

  def setStatusMsg(msg: String): Option[Exception]
}
