package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.repository.MessageRepo
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class DeleteMsgUseCaseImpl(implicit messageRepo: MessageRepo) extends DeleteMsgUseCase {

  override def deleteAllMessages(friendName: String): Future[Unit] = Future {
    messageRepo.deleteAllMessages(friendName) match {
      case Some(err) => error(s"[!] ${err.getMessage}", err.getCause); throw err
      case None      => info(s"[+] deleted all messages with $friendName")
    }
  }
}
