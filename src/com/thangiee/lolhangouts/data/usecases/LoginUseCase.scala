package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.cache.{CacheKey, PrefsCache}
import com.thangiee.lolhangouts.data.datasources.entities.mappers.AppDataMapper
import com.thangiee.lolhangouts.data.datasources.AppDataFactory
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.Region
import com.thangiee.lolhangouts.data.usecases.entities.Value.Boolean.IsLoginOffline
import com.thangiee.lolhangouts.data.usecases.entities.Value.String.{Password, Username, Version}
import com.thangiee.lolhangouts.data.exception.UseCaseException._
import com.thangiee.lolhangouts.data.exception.UserInputException._
import com.thangiee.lolhangouts.data.exception.{UseCaseException, UserInputException}
import thangiee.riotapi.core.RiotApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LoginUseCase extends Interactor {
  def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)]
  def login(user: String, pass: String): Future[Unit]
  def saveLoginInfo(user: String, pass: String, isLoginOffline: Boolean, isGuestMode: Boolean): Future[Unit]
  def updateAppVersion(version: String): Future[Unit]
  def loadAppVersion(): Future[Version]
}

case class LoginUseCaseImpl() extends LoginUseCase {

  override def login(user: String, pass: String): Future[Unit] = Future {
    // validate inputs
    if (user.isEmpty) UserInputException("[-] username is empty", EmptyUsername).logThenThrow.i
    if (pass.isEmpty) UserInputException("[-] password is empty", EmptyPassword).logThenThrow.i

    val region = Region.getFromId(PrefsCache.getString(CacheKey.LoginRegionId).getOrElse(""))
    RiotApi.regionId = region.id

    if (!LoLChat.connect(region))
      UseCaseException("[-] Fail to connect to server.", ConnectionError).logThenThrow.i

    if (!LoLChat.login(user, pass))
      UseCaseException("[-] Invalid username/password", AuthenticationError).logThenThrow.i
  }

  override def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)] = Future {
    val data = AppDataMapper.transform(AppDataFactory().createAppDataEntity())
    (data.saveUsername, data.savePassword, data.selectedRegion, data.isLoginOffline)
  }

  override def saveLoginInfo(user: String, pass: String, isLoginOffline: Boolean, isGuestMode: Boolean): Future[Unit] = Future {
    PrefsCache.put(CacheKey.LoginName → user)
    PrefsCache.put(CacheKey.LoginPass → pass)
    PrefsCache.put(CacheKey.IsLoginOffline → isLoginOffline)
    PrefsCache.put(CacheKey.IsGuestMode → isGuestMode)
  }

  override def updateAppVersion(version: String): Future[Unit] = Future {
    PrefsCache.put(CacheKey.AppVersion → version)
  }

  override def loadAppVersion(): Future[Version] = Future {
    PrefsCache.getString(CacheKey.AppVersion).getOrElse("-1")
  }
}