package com.thangiee.lolhangouts.data.datasources

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.AppDataEntity

case class AppDataFactory() {

  def createAppDataEntity(): AppDataEntity = {
    AppDataEntity(
      LoLChat.sessions.headOption.map { case (user, _) => user } getOrElse Cached.loginUsername,
      Cached.loginPassword,
      Cached.appVersion.getOrElse("0"),
      Cached.isLoginOffline,
      Cached.loginRegionId,
      Cached.isGuessMode
    )
  }
}
