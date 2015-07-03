package com.thangiee.lolhangouts

import com.thangiee.lolhangouts.data.datasources.cache.PrefsCache

package object data {

  object Cached {
    def loginUsername: String = PrefsCache.getString("login-username").getOrElse("")
    def loginUsername_=(name: String): Unit = PrefsCache.put("login-username" → name)

    def loginPassword: String = PrefsCache.getString("login-password").getOrElse("")
    def loginPassword_=(pass: String): Unit = PrefsCache.put("login-password" → pass)

    def appVersion: Option[String] = PrefsCache.getString("app-version")
    def appVersion_=(version: String): Unit = PrefsCache.put("app-version" → version)

    def loginRegionId: Option[String] = PrefsCache.getString("login-region-id")
    def loginRegionId_=(regionId: String): Unit = PrefsCache.put("login-region-id" → regionId)

    def isLoginOffline: Boolean = PrefsCache.getBoolean("login-offline", defValue = false)
    def isLoginOffline_=(b: Boolean): Unit = PrefsCache.put("login-offline" → b)

    def isGuessMode: Boolean = PrefsCache.getBoolean("guest-mode", defValue = false)
    def isGuessMode_=(b: Boolean): Unit = PrefsCache.put("guest-mode" → b)

    def isFirstLaunch: Boolean = PrefsCache.getBoolean("first_launch", defValue = true)
    def isFirstLaunch_=(b: Boolean): Unit = PrefsCache.put("first_launch" → b)

    def isAdsEnable: Boolean = PrefsCache.getBoolean("is_ads_enable", defValue = true)
    def isAdsEnable_=(b: Boolean): Unit = PrefsCache.put("is_ads_enable" → b)

    def inGameName(key: String): Option[String] = PrefsCache.getString(s"inGameName-$key")
    def inGameName_=(keyVal: (String, String)): Unit = PrefsCache.put(s"inGameName-${keyVal._1.toLowerCase}" → keyVal._2)

    def friendChat(key: String): Option[String] = PrefsCache.getString(s"friendChat-$key")
    def friendChat_=(keyVal: (String, String)): Unit = PrefsCache.put(s"friendChat-${keyVal._1.toLowerCase}" → keyVal._2)

    def statusMsg(key: String): Option[String] = PrefsCache.getString(s"statusMsg-$key")
    def statusMsg_=(keyVal: (String, String)): Unit = PrefsCache.put(s"statusMsg-${keyVal._1.toLowerCase}" → keyVal._2)
  }
}
