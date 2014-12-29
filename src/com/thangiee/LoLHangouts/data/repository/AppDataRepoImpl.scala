package com.thangiee.LoLHangouts.data.repository

import android.content.Context
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.mappers.AppDataMapper
import com.thangiee.LoLHangouts.data.repository.datasources.AppDataFactory
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.domain.entities.{AppData, Region}
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import thangiee.riotapi.core.RiotApi

case class AppDataRepoImpl(implicit ctx: Context) extends AppDataRepo {

  override def getAppData: Either[ErrorBundle, AppData] = {
    Right(AppDataMapper().transform(AppDataFactory(ctx).createAppDataEntity()))
  }

  override def updateAppVersion(): Unit = {
    val currentVersion = ctx.getPackageManager.getPackageInfo(ctx.getPackageName, 0).versionName
    PrefsCache.put[String](CacheKey.AppVersion → currentVersion)
  }

  override def saveRegion(region: Region): Unit = {
    PrefsCache.put(CacheKey.LoginRegionId → region.id)
    RiotApi.region(region.id)
  }

  override def savePassword(password: String): Unit = PrefsCache.put(CacheKey.LoginPass → password)

  override def setLoginOffline(isEnable: Boolean): Unit = PrefsCache.put(CacheKey.LoginOffline → isEnable)

  override def saveUsername(username: String): Unit = PrefsCache.put(CacheKey.LoginName → username)
}
