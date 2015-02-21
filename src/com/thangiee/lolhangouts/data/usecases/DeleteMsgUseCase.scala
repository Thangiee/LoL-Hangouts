package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data.utils._

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