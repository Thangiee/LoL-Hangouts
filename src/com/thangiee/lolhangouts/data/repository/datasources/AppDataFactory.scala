package com.thangiee.lolhangouts.data.repository.datasources

import com.thangiee.lolhangouts.data.cache.PrefsCache
import com.thangiee.lolhangouts.data.entities.AppDataEntity
import com.thangiee.lolhangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat

case class AppDataFactory() {

  def createAppDataEntity(): AppDataEntity = {
    AppDataEntity(
      if (LoLChat.isLogin) LoLChat.loginName else PrefsCache.getString(CacheKey.LoginName).getOrElse(""),
      PrefsCache.getString(CacheKey.LoginPass).getOrElse(""),
      PrefsCache.getString(CacheKey.AppVersion).getOrElse("-1"),
      PrefsCache.getBoolean(CacheKey.IsLoginOffline, defValue = false),
      PrefsCache.getString(CacheKey.LoginRegionId)
    )
  }
}
