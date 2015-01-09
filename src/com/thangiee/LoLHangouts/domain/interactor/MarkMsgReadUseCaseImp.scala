package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.MessageRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class MarkMsgReadUseCaseImp(implicit messageRepo: MessageRepo) extends MarkMsgReadUseCase {

  override def markAsRead(friendName: String): Future[Unit] = Future {
    messageRepo.setMessagesRead(friendName) match {
      case Some(err) => error(s"[!] ${err.getMessage}", err.getCause); throw err
      case None      => info("[+] Messages set to read")
    }
  }
}
