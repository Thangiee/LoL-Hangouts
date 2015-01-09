package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Message
import com.thangiee.LoLHangouts.domain.repository.MessageRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class GetMsgUseCaseImpl(implicit messageRepo: MessageRepo) extends GetMsgUseCase {

  override def loadAllMessages: Future[List[Message]] = Future {
    messageRepo.getAllMessages.fold(
      e => logAndThrow(e),
      m => logAndReturn(m)
    )
  }

  override def loadMessages(friendName: String, n: Int): Future[List[Message]] = Future {
    messageRepo.getMessages(friendName, n).fold(
      e => logAndThrow(e),
      m => logAndReturn(m)
    )
  }

  override def loadLastMessage(friendName: String): Future[Option[Message]] = Future{
    messageRepo.getLastMessage(friendName).fold(
      e => logAndThrow(e),
      m => m
    )
  }

  override def loadUnreadMessages(n: Int): Future[List[Message]] = Future {
    messageRepo.getUnreadMessages(n).fold(
      e => logAndThrow(e),
      m => logAndReturn(m)
    )
  }

  override def loadUnreadMessages(friendName: String): Future[List[Message]] = Future {
    messageRepo.getUnreadMessages(friendName).fold(
      e => logAndThrow(e),
      m => logAndReturn(m)
    )
  }

  private def logAndThrow(e: Exception) = {
    error(s"[!] ${e.getMessage}", e.getCause)
    throw e
  }

  private def logAndReturn(m: List[Message]): List[Message] = {
    info(s"[+] ${m.size} messages loaded")
    m
  }
}
