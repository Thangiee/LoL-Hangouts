package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Message
import com.thangiee.lolhangouts.domain.exception.UserInputException
import com.thangiee.lolhangouts.domain.repository.MessageRepo
import com.thangiee.lolhangouts.domain.utils._

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
