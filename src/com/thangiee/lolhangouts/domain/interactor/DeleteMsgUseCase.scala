package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.repository.datasources.sqlite.DB
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DeleteMsgUseCase extends Interactor {
  def deleteAllMessages(friendName: String): Future[Unit]
}

case class DeleteMsgUseCaseImpl() extends DeleteMsgUseCase {

  override def deleteAllMessages(friendName: String): Future[Unit] = Future {
    DB.deleteMessages(LoLChat.loginName, friendName)
    info(s"[+] deleted all messages with $friendName")
  }
}