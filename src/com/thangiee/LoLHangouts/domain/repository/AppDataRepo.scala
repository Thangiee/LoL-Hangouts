package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.{Region, AppData}
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle

trait AppDataRepo {
  def getAppData: Either[ErrorBundle, AppData]

  def updateAppVersion(): Unit

  def saveUsername(username: String)

  def savePassword(password: String)

  def saveRegion(region: Region)
  
  def setLoginOffline(isEnable: Boolean)
}
