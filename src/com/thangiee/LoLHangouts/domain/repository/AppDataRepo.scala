package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.{AppData, Region}

trait AppDataRepo {
  def getAppData: Either[Exception, AppData]

  def updateAppVersion(): Option[Exception]

  def saveUsername(username: String): Option[Exception]

  def savePassword(password: String): Option[Exception]

  def saveRegion(region: Region): Option[Exception]
  
  def setLoginOffline(isEnable: Boolean): Option[Exception]
}
