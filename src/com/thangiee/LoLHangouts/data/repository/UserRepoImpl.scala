package com.thangiee.LoLHangouts.data.repository

import android.content.Context
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.UserMapper
import com.thangiee.LoLHangouts.data.repository.datasources.UserFactory
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.domain.entities.{Region, User}
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.repository.UserRepo

case class UserRepoImpl(implicit ctx: Context) extends UserRepo {
  /**
   * try to login a user and set it as the active user
   */
  override def loginUser(username: String, password: String): Option[ErrorBundle] = {
    val region = Region.getFromId(PrefsCache.getString(CacheKey.LoginRegionId).getOrElse(""))
    if (!LoLChat.connect(region)) {
      return Some(ErrorBundle("Fail to connect to server."))
    }

    if (LoLChat.login(username, password)) {
      None // no error, successful login
    } else {
      Some(ErrorBundle("Invalid username/password"))
    }
  }

  /**
   * log out, if any, the currently logged in user
   */
  override def logoutUser(): Unit = {
    LoLChat.disconnect()
  }

  /**
   * @return the user that is currently login or the user that is most recently saved
   */
  override def getActiveUser: Either[ErrorBundle, User] = {
    Right(UserMapper().transform(UserFactory().createUserEntity()))
  }
}
