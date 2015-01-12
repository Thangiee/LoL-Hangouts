package com.thangiee.LoLHangouts.data.repository.datasources

import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.AppDataEntity
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat

case class AppDataFactory() {

  def createAppDataEntity(): Either[Exception, AppDataEntity] = Right {
    AppDataEntity(
      if (LoLChat.isLogin) LoLChat.loginName() else PrefsCache.getString(CacheKey.LoginName).getOrElse(""),
      PrefsCache.getString(CacheKey.LoginPass).getOrElse(""),
      PrefsCache.getString(CacheKey.AppVersion).getOrElse("-1"),
      PrefsCache.getBoolean(CacheKey.LoginOffline, defValue = false),
      PrefsCache.getString(CacheKey.LoginRegionId)
    )
  }
}
