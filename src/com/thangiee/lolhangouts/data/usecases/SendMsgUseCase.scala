package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.entities.MessageEntity
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.Message
import com.thangiee.lolhangouts.data.exception.UseCaseException.MessageSentError
import com.thangiee.lolhangouts.data.exception.UserInputException.EmptyMessage
import com.thangiee.lolhangouts.data.exception.{UseCaseException, UserInputException}
import com.thangiee.lolhangouts.data.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait SendMsgUseCase extends Interactor {
  def sendMessage(message: Message): Future[Unit]
}

case class SendMsgUseCaseImpl() extends SendMsgUseCase {

  override def sendMessage(msg: Message): Future[Unit] = Future {
    if (msg.text.isEmpty) UserInputException("[-] Can't send an empty message", EmptyMessage).logThenThrow.i

    LoLChat.getFriendByName(msg.friendName) match {
      case Some(f) =>
        if (LoLChat.sendMessage(f, msg.text)) {
          info("[+] Message sent!")
          new MessageEntity(LoLChat.loginName, msg.friendName, msg.text, msg.isSentByUser, msg.isRead, msg.date).save()
          info("[+] new message saved to database")
        } else {
          UseCaseException("[!] LoLChat failed to send the message", MessageSentError).logThenThrow.w
        }
      case None    =>
        val errMsg = s"[-] Can't send message because ${msg.friendName} is not on the friend list"
        UseCaseException(errMsg, MessageSentError).logThenThrow.i
    }
  }
}