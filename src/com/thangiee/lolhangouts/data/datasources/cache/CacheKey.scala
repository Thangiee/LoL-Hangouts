package com.thangiee.lolhangouts.data.datasources.cache

object CacheKey {
  val LoginName = "login-username"
  val LoginPass = "login-password"
  val AppVersion = "app-version"
  val IsLoginOffline = "login-offline"
  val LoginRegionId = "login-region-id"

  val IsFirstLaunch = "first_launch"
  val IsAdsEnable = "is_ads_enable"

  def friendChat(key: String) = "friendChat-" + key
  def statusMsg(key: String) = "statusMsg-" + key
}
