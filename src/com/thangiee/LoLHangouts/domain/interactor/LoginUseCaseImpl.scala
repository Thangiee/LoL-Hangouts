package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.exception.{ConnectionException, AuthorizationException}
import com.thangiee.LoLHangouts.domain.repository.{AppDataRepo, UserRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LoginUseCaseImpl(implicit userRepo: UserRepo, appDataRepo: AppDataRepo) extends LoginUseCase {

  override def login(user: String, pass: String): Unit = Future {
    // validate inputs
    if (user.isEmpty) blankUsernameErrorListener.notify("username can't be empty")
    if (pass.isEmpty) blankPasswordErrorListener.notify("password can't be empty")

    if (!user.isEmpty && !pass.isEmpty) {
      userRepo.loginUser(user, pass).map {
        case e: AuthorizationException => authorizationErrorListener.notify(e.getMessage)
        case e: ConnectionException    => connectionErrorListener.notify(e.getMessage)
        case e: Exception              => throw e
      }.getOrElse(loginListener.notify(Unit))
    }
  }

  override def loadLoginInfo(): Unit = Future {
    appDataRepo.getAppData.fold(
      error => throw error,
      data => loadLoginInfoListener.notify((data.saveUsername, data.savePassword, data.selectedRegion, data.isLoginOffline))
    )
  }

  override def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Unit = Future {
    appDataRepo.saveUsername(username)
    appDataRepo.savePassword(password)
    appDataRepo.setLoginOffline(isLoginOffline)
    saveLoginInfoListener.notify(Unit)
  }
}

