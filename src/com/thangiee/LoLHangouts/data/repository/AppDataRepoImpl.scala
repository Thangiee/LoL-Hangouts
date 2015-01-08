package com.thangiee.LoLHangouts.data.repository

import android.content.Context
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.AppDataMapper
import com.thangiee.LoLHangouts.data.repository.datasources.AppDataFactory
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.domain.entities.{AppData, Region}
import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import thangiee.riotapi.core.RiotApi

case class AppDataRepoImpl(implicit ctx: Context) extends AppDataRepo {

  override def getAppData: Either[Exception, AppData] = {
    AppDataFactory(ctx).createAppDataEntity().right.map(AppDataMapper.transform)
  }

  override def updateAppVersion(): Option[Exception] = {
    val currentVersion = ctx.getPackageManager.getPackageInfo(ctx.getPackageName, 0).versionName
    PrefsCache.put[String](CacheKey.AppVersion → currentVersion)
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
