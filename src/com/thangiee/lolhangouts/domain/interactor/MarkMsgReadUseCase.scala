package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.repository.datasources.sqlite.DB

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MarkMsgReadUseCase extends Interactor {
  def markAsRead(friendName: String): Future[Unit]
}

case class MarkMsgReadUseCaseImp() extends MarkMsgReadUseCase {

  override def markAsRead(friendName: String): Future[Unit] = Future {
    DB.getUnreadMessages(LoLChat.loginName, friendName).map(_.setRead(true).save())
      .logThenReturn(_ => "[+] Messages set to read")
  }
}