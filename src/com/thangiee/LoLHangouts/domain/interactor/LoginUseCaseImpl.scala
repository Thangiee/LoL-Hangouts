package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.repository.{AppDataRepo, UserRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LoginUseCaseImpl(implicit userRepo: UserRepo, appDataRepo: AppDataRepo) extends LoginUseCase {

  override def login(user: String, pass: String): Unit = Future {
    // validate inputs
    if (user.isEmpty) errorListener.notify(ErrorBundle("username can't be empty"))
    if (pass.isEmpty) errorListener.notify(ErrorBundle("password can't be empty"))

    if (!user.isEmpty && !pass.isEmpty) {
      userRepo.loginUser(user, pass)
        .map(e => errorListener.notify(e))
        .getOrElse(loginListener.notify(Unit))
    }
  }

  override def loadLoginInfo(): Unit = Future {
    appDataRepo.getAppData.fold(
      error => errorListener.notify(error),
      data  => loadLoginInfoListener.notify((data.saveUsername, data.savePassword, data.selectedRegion, data.isLoginOffline))
    )
  }

  override def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Unit = Future {
    appDataRepo.saveUsername(username)
    appDataRepo.savePassword(password)
    appDataRepo.setLoginOffline(isLoginOffline)
    saveLoginInfoListener.notify(Unit)
  }
}

