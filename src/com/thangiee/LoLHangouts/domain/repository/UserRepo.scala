package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.User
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle

trait UserRepo {

  /**
   * try to login a user and set it as the active user
   *
   * @param username
   * @param password
   * @return
   */
  def loginUser(username: String, password: String): Option[ErrorBundle]

  /**
   * log out, if any, the currently logged in user
   */
  def logoutUser()

  /**
   * @return the user that is currently login or the user that is most recently saved
   */
  def getActiveUser: Either[ErrorBundle, User]
}
