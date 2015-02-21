package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.cache.{CacheKey, PrefsCache}
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.exception.UseCaseException
import com.thangiee.lolhangouts.data.exception.UseCaseException.InternalError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ChangeUserStatusCase extends Interactor {
  def appearOnline(): Future[Unit]
  def appearAway(): Future[Unit]
  def appearOffline(): Future[Unit]
  def setStatusMsg(msg: String): Future[Unit]
}

case class ChangeUserStatusCaseImpl() extends ChangeUserStatusCase {

  override def appearOnline(): Future[Unit] = Future {
    if (LoLChat.isLogin) {
      LoLChat.appearOnline()
    } else {
      UseCaseException("[!] LoLChat is not login", InternalError).logThenThrow.w
    }
  }

  override def appearOffline(): Future[Unit] = Future {
    if (LoLChat.isLogin) {
      LoLChat.appearOffline()
    } else {
      UseCaseException("[!] LoLChat is not login", InternalError).logThenThrow.w
    }
  }

  override def appearAway(): Future[Unit] = Future {
    if (LoLChat.isLogin) {
      LoLChat.appearOffline()
    } else {
      UseCaseException("[!] LoLChat is not login", InternalError).logThenThrow.w
    }
  }

  override def setStatusMsg(msg: String): Future[Unit] =Future {
    if (LoLChat.isLogin) {
      PrefsCache.put[String](CacheKey.statusMsg(LoLChat.summId) → msg)
      LoLChat.changeStatusMsg(msg)
    } else {
      UseCaseException("[!] LoLChat is not login", InternalError).logThenThrow.w
    }
  }
}