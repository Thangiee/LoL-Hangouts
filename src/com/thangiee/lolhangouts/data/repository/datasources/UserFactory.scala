package com.thangiee.lolhangouts.data.repository.datasources

import com.thangiee.lolhangouts.data.cache.PrefsCache
import com.thangiee.lolhangouts.data.entities.UserEntity
import com.thangiee.lolhangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.lolhangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import thangiee.riotapi.core.RiotApi

case class UserFactory() {

  def createUserEntity(): Either[Exception, UserEntity] = {
    val regionId = if (LoLChat.isConnected) {
      LoLChat.region.id
    } else {
      PrefsCache.getString(CacheKey.LoginRegionId)
        .getOrElse(return Left(new IllegalStateException("Cant find region id")))
    }

    val statusMsg = PrefsCache.getString(CacheKey.statusMsg(LoLChat.summId))
      .getOrElse(LoLChat.statusMsg)

    Right {
      UserEntity(
        LoLChat.loginName,
        inGameName,
        regionId,
        statusMsg,
        PrefsCache.getString(CacheKey.friendChat(LoLChat.loginName))
      )
    }
  }

  private def inGameName: String = {
    val cacheKey = s"inGameName-${LoLChat.loginName.toLowerCase}"
    implicit val caller = new CachingApiCaller()

    RiotApi.summonerNameById(LoLChat.summId.toLong) match {
      case Right(name) => // successful api call
        PrefsCache.put(cacheKey → name) // save it for backup
        name
      case Left(error) => // api call failed
        // try to use the backup. If backup fail too, use what the user provided to login
        PrefsCache.getString(cacheKey).getOrElse(LoLChat.loginName)
    }
  }
}
