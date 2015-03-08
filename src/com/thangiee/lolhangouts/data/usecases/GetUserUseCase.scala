package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.cache.{CacheKey, PrefsCache}
import com.thangiee.lolhangouts.data.datasources.entities.UserEntity
import com.thangiee.lolhangouts.data.datasources.entities.mappers.UserMapper
import com.thangiee.lolhangouts.data.datasources.cachingApiCaller
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.User
import com.thangiee.lolhangouts.data.exception.UseCaseException
import com.thangiee.lolhangouts.data.exception.UseCaseException.InternalError
import thangiee.riotapi.core.RiotApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait GetUserUseCase extends Interactor {

  def loadUser(): Future[User]
}

case class GetUserUseCaseImpl() extends GetUserUseCase {

  override def loadUser(): Future[User] = Future {
    val regionId = if (LoLChat.isConnected) {
      LoLChat.region.id
    } else {
      PrefsCache.getString(CacheKey.LoginRegionId)
        .getOrElse(UseCaseException("[!] Cant find region id", InternalError).logThenThrow.w)
    }

    val statusMsg = PrefsCache.getString(CacheKey.statusMsg(LoLChat.summId))
      .getOrElse(LoLChat.statusMsg)

    UserMapper.transform {
      UserEntity(
        LoLChat.loginName,
        inGameName(regionId),
        regionId,
        statusMsg,
        PrefsCache.getString(CacheKey.friendChat(LoLChat.loginName))
      )
    }
  }

  private def inGameName(regionId: String): String = {
    val cacheKey = s"inGameName-${LoLChat.loginName.toLowerCase}"

    RiotApi.summonerNameById(LoLChat.summId.toLong, regionId) map {
      name => // successful api call
        PrefsCache.put(cacheKey → name) // save it for backup
        name
    } recover {
      case e: Exception => // api call failed
        // try to use the backup. If backup fail too, use what the user provided to login
        PrefsCache.getString(cacheKey).getOrElse(LoLChat.loginName)
    } get
  }
}