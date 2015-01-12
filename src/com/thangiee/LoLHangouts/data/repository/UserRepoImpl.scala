package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.UserMapper
import com.thangiee.LoLHangouts.data.repository.datasources.UserFactory
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.domain.entities.{Region, User}
import com.thangiee.LoLHangouts.domain.exception.{AuthorizationException, ConnectionException}
import com.thangiee.LoLHangouts.domain.repository.UserRepo

case class UserRepoImpl() extends UserRepo {
  /**
   * try to login a user and set it as the active user
   */
  override def loginUser(username: String, password: String): Option[Exception] = {
    val region = Region.getFromId(PrefsCache.getString(CacheKey.LoginRegionId).getOrElse(""))
    if (!LoLChat.connect(region)) {
      return Some(new ConnectionException("Fail to connect to server."))
    }

    if (LoLChat.login(username, password)) {
      None // no error, successful login
    } else {
      Some(new AuthorizationException("Invalid username/password"))
    }
  }

  /**
   * log out, if any, the currently logged in user
   */
  override def logoutUser(): Option[Exception] = {
    LoLChat.disconnect()
    None
  }

  /**
   * @return the user that is currently login or the user that is most recently saved
   */
  override def getActiveUser: Either[Exception, User] = {
    UserFactory().createUserEntity().right.map(UserMapper.transform)
  }

  override def setAppearanceOnline(): Option[Exception] = {
    if (!LoLChat.isLogin) {
      Some(new IllegalStateException("LoLChat is not login"))
    } else {
      LoLChat.appearOnline()
      None
    }
  }

  override def setAppearanceOffline(): Option[Exception] = {
    if (!LoLChat.isLogin) {
      Some(new IllegalStateException("LoLChat is not login"))
    } else {
      LoLChat.appearOffline()
      None
    }
  }

  override def setAppearanceAway(): Option[Exception] = {
    if (!LoLChat.isLogin) {
      Some(new IllegalStateException("LoLChat is not login"))
    } else {
      LoLChat.appearAway()
      None
    }
  }

  override def setStatusMsg(msg: String): Option[Exception] = {
    if (!LoLChat.isLogin) {
      Some(new IllegalStateException("LoLChat is not login"))
    } else {
      LoLChat.changeStatusMsg(msg)
      None
    }
  }
}
