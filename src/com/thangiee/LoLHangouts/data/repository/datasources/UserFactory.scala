package com.thangiee.LoLHangouts.data.repository.datasources

import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.entities.UserEntity
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import thangiee.riotapi.core.RiotApi

case class UserFactory() {

  def createUserEntity(): Either[Exception, UserEntity] = {
    val regionId = if (LoLChat.isConnected) {
      LoLChat.region().id
    } else {
      PrefsCache.getString(CacheKey.LoginRegionId)
        .getOrElse(return Left(new IllegalStateException("Cant find region id")))
    }

    Right {
      UserEntity(
        LoLChat.loginName(),
        inGameName,
        regionId,
        LoLChat.statusMsg(),
        PrefsCache.getString(s"friendChat-${LoLChat.loginName()}")
      )
    }
  }

  private def inGameName: String = {
    val cacheKey = s"inGameName-${LoLChat.loginName().toLowerCase}"
    implicit val caller = new CachingApiCaller()

    RiotApi.summonerNameById(LoLChat.summonerId().getOrElse("-1").toLong) match {
      case Right(name) => // successful api call
        PrefsCache.put(cacheKey → name) // save it for backup
        name
      case Left(error) => // api call failed
        // try to use the backup. If backup fail too, use what the user provided to login
        PrefsCache.getString(cacheKey).getOrElse(LoLChat.loginName())
    }
  }
}
