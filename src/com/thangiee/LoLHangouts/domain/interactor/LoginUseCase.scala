package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Region
import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsLoginOffline
import com.thangiee.LoLHangouts.domain.entities.Value.String.{Version, Password, Username}

import scala.concurrent.Future

trait LoginUseCase extends Interactor {

  def loadLoginInfo(): Future[(Username, Password, Option[Region], IsLoginOffline)]

  def login(user: String, pass: String): Future[Unit]

  def saveLoginInfo(username: String, password: String, isLoginOffline: Boolean): Future[Unit]

  def updateAppVersion(version: String): Future[Unit]

  def loadAppVersion(): Future[Version]
}
