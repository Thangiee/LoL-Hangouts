package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.{FailAuthentication, NotConnected, UnexpectedError}
import com.thangiee.lolchat.region._
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.mappers.AppDataMapper
import com.thangiee.lolhangouts.data.datasources.{AppDataFactory, _}
import com.thangiee.lolhangouts.data.usecases.LoginUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.Value.Boolean.IsLoginOffline
import com.thangiee.lolhangouts.data.usecases.entities.Value.String.{Password, Username, Version}
import com.thangiee.lolhangouts.data._
import org.scalactic.Or

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LoginUseCase extends Interactor {
  def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)]
  def login(user: String, pass: String, isLoginOffline: Boolean): Future[Unit Or LoginError]
  def saveLoginInfo(user: String, pass: String, isLoginOffline: Boolean, isGuestMode: Boolean): Future[Unit]
  def updateAppVersion(version: String): Future[Unit]
  def loadAppVersion(): Future[Version]
}

object LoginUseCase {
  sealed trait LoginError
  object EmptyUsername extends LoginError
  object EmptyPassword extends LoginError
  object EmptyUserAndPass extends LoginError
  object ConnectionError extends LoginError
  object AuthenticationError extends LoginError
  object InternalError extends LoginError
}

case class LoginUseCaseImpl() extends LoginUseCase {

  override def login(user: String, pass: String, isLoginOffline: Boolean): Future[Unit Or LoginError] = Future {
    (user.isEmpty, pass.isEmpty) match {
      case (true, false)  => info("[-] username is empty"); Bad(EmptyUsername)
      case (false, true)  => info("[-] password is empty"); Bad(EmptyPassword)
      case (true, true)   => info("[-] username and password are empty"); Bad(EmptyUserAndPass)
      case (false, false) =>
        val region = getFromId(Cached.loginRegionId.getOrElse("na"))
        LoLChat.login(user, pass, region, isLoginOffline) match {
          case Good(_)                       => Good(Unit)
          case Bad(NotConnected(url))        => info(s"[-] fail to reach $url"); Bad(ConnectionError)
          case Bad(FailAuthentication(_, _)) => info("[-] Invalid username or password"); Bad(AuthenticationError)
          case Bad(UnexpectedError(t))       => t.printStackTrace(); Bad(InternalError)
        }
    }
  }

  override def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)] = Future {
    val data = AppDataMapper.transform(AppDataFactory().createAppDataEntity())
    (data.saveUsername, data.savePassword, data.selectedRegion, data.isLoginOffline)
  }

  override def saveLoginInfo(user: String, pass: String, isLoginOffline: Boolean, isGuestMode: Boolean): Future[Unit] = Future {
    Cached.loginUsername = user
    Cached.loginPassword = pass
    Cached.isLoginOffline = isLoginOffline
    Cached.isGuessMode = isGuestMode
  }

  override def updateAppVersion(version: String): Future[Unit] = Future { Cached.appVersion = version }

  override def loadAppVersion(): Future[Version] = Future { Cached.appVersion.getOrElse("0") }
}