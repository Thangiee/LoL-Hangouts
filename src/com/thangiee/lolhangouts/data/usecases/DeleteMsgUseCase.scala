package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DeleteMsgUseCase extends Interactor {
  def deleteAllMessages(friendName: String): Future[Unit]
}

case class DeleteMsgUseCaseImpl() extends DeleteMsgUseCase {

  override def deleteAllMessages(friendName: String): Future[Unit] = Future {
    DB.deleteMessages(Cached.loginUsername, friendName)
    info(s"[+] deleted all messages with $friendName")
  }
}