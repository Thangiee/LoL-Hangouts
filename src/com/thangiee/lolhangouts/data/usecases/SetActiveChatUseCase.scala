package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.cache.{CacheKey, PrefsCache}
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.exception.UseCaseException
import com.thangiee.lolhangouts.data.exception.UseCaseException.InternalError
import com.thangiee.lolhangouts.data.utils._

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
