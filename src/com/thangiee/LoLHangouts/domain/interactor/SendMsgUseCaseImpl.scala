package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Message
import com.thangiee.LoLHangouts.domain.exception.UserInputException
import com.thangiee.LoLHangouts.domain.repository.MessageRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class SendMsgUseCaseImpl(implicit messageRepo: MessageRepo) extends SendMsgUseCase {
  override def sendMessage(message: Message): Future[Unit] = Future {
    if (message.text.isEmpty) throw UserInputException("Can't send an empty message")

    messageRepo.sendMessage(message) match {
      case Some(err) => error(s"[!] ${err.getMessage}", err.getCause); throw err
      case None      =>
        info("[+] Message sent!")
        messageRepo.saveMessages(List(message)) match {
          case Some(err) => error(s"[!] ${err.getMessage}", err.getCause); throw err
          case None      => info("[+] 1 message saved to database")
        }
    }
  }
}
