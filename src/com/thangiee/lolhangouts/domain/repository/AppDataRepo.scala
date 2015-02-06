package com.thangiee.lolhangouts.domain.repository

import com.thangiee.lolhangouts.domain.entities.{AppData, Region}

trait AppDataRepo {
  def getAppData: Either[Exception, AppData]

  def updateAppVersion(version: String): Option[Exception]

  def saveUsername(username: String): Option[Exception]

  def savePassword(password: String): Option[Exception]

  def saveRegion(region: Region): Option[Exception]
  
  def setLoginOffline(isEnable: Boolean): Option[Exception]
}
