package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.AppDataMapper
import com.thangiee.LoLHangouts.data.repository.datasources.AppDataFactory
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.domain.entities.{AppData, Region}
import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import thangiee.riotapi.core.RiotApi

trait AppDataRepoImpl extends AppDataRepo {

  override def getAppData: Either[Exception, AppData] = {
    AppDataFactory().createAppDataEntity().right.map(AppDataMapper.transform)
  }

  override def updateAppVersion(version: String): Option[Exception] = {
    PrefsCache.put[String](CacheKey.AppVersion → version)
    None
  }

  override def saveRegion(region: Region): Option[Exception] = {
    PrefsCache.put(CacheKey.LoginRegionId → region.id)
    RiotApi.region(region.id)
    None
  }

  override def savePassword(password: String): Option[Exception] = {
    PrefsCache.put(CacheKey.LoginPass → password)
    None
  }

  override def setLoginOffline(isEnable: Boolean): Option[Exception] = {
    PrefsCache.put(CacheKey.LoginOffline → isEnable)
    None
  }

  override def saveUsername(username: String): Option[Exception] = {
    PrefsCache.put(CacheKey.LoginName → username)
    None
  }
}

object AppDataRepoImpl extends AppDataRepoImpl
