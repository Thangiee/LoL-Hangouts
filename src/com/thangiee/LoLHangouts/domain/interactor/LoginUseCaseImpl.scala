package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Region
import com.thangiee.LoLHangouts.domain.entities.Value.Boolean._
import com.thangiee.LoLHangouts.domain.entities.Value.String._
import com.thangiee.LoLHangouts.domain.exception.{UserInputException, AuthorizationException, ConnectionException}
import com.thangiee.LoLHangouts.domain.repository.{AppDataRepo, UserRepo}
import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LoginUseCaseImpl(implicit userRepo: UserRepo, appDataRepo: AppDataRepo) extends LoginUseCase {

  override def login(user: String, pass: String): Future[Unit] = Future {
    // validate inputs
    if (user.isEmpty) throw UserInputException("username can't be empty")
    if (pass.isEmpty) throw UserInputException("password can't be empty")

    if (!user.isEmpty && !pass.isEmpty) {
      userRepo.loginUser(user, pass).map {
        case e: AuthorizationException => info(s"[-] ${e.getMessage}"); throw e
        case e: ConnectionException    => info(s"[-] ${e.getMessage}"); throw e
        case e: Exception              => error(s"[!] ${e.getMessage}", e.getCause); throw e
      }
    }
  }

  override def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)] = Future {
    appDataRepo.getAppData.fold(
      e    => { error(s"[!] ${e.getMessage}", e.getCause); throw e },
      data => (data.saveUsername, data.savePassword, data.selectedRegion, data.isLoginOffline)
    )
  }

  override def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Future[Unit] = Future {
    appDataRepo.saveUsername(username)
    appDataRepo.savePassword(password)
    appDataRepo.setLoginOffline(isLoginOffline)
  }
}
