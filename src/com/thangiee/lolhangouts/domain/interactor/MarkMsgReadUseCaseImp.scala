package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.repository.MessageRepo
import com.thangiee.lolhangouts.domain.utils._

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
