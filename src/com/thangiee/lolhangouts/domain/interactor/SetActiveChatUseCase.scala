package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.data.cache.PrefsCache
import com.thangiee.lolhangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.domain.entities.Friend
import com.thangiee.lolhangouts.domain.exception.UseCaseException
import com.thangiee.lolhangouts.domain.exception.UseCaseException.InternalError
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait SetActiveChatUseCase extends Interactor {
  def setActiveChat(friend: Friend): Future[Unit]
}

case class SetActiveChatUseCaseImpl() extends SetActiveChatUseCase {

  override def setActiveChat(friend: Friend): Future[Unit] = Future {
    if (!LoLChat.isLogin) {
      UseCaseException("[!] LoLChat is not login", InternalError).logThenThrow.w
    } else {
      PrefsCache.put[String](CacheKey.friendChat(LoLChat.loginName) → friend.name)
      info(s"[+] active chat set to ${friend.name}")
    }
  }
}
