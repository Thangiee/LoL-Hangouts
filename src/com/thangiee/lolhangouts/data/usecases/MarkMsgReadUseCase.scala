package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.sqlite.DB

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MarkMsgReadUseCase extends Interactor {
  def markAsRead(friendName: String): Future[Unit]
}

case class MarkMsgReadUseCaseImp() extends MarkMsgReadUseCase {

  override def markAsRead(friendName: String): Future[Unit] = Future {
    DB.getUnreadMessages(Cached.loginUsername, friendName).map(_.setRead(true).save())
      .logThenReturn(_ => "[+] Messages set to read")
  }
}