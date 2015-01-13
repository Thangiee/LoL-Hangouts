package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.MessageRepo
import com.thangiee.LoLHangouts.domain.utils._

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
