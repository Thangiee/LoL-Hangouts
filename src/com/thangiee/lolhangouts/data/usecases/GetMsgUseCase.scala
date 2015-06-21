package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.mappers.MessageMapper
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data.usecases.entities.Message

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GetMsgUseCase extends Interactor {
  def loadAllMessages: Future[List[Message]]
  def loadMessages(friendName: String, n: Int = Integer.MAX_VALUE): Future[List[Message]]
  def loadUnreadMessages(n: Int): Future[List[Message]]
  def loadUnreadMessages(friendName: String): Future[List[Message]]
  def loadLastMessage(friendName: String): Future[Option[Message]]
}

case class GetMsgUseCaseImpl() extends GetMsgUseCase {

  override def loadAllMessages: Future[List[Message]] = Future {
    DB.getAllMessages.map(MessageMapper.transform).logThenReturn((msgs) => s"[+] ${msgs.size} messages loaded")
  }

  override def loadMessages(friendName: String, n: Int): Future[List[Message]] = Future {
    DB.getMessages(Cached.loginUsername, friendName, n).map(MessageMapper.transform)
  }

  override def loadLastMessage(friendName: String): Future[Option[Message]] = Future {
    DB.getLatestMessage(Cached.loginUsername, friendName).map(MessageMapper.transform)
  }

  override def loadUnreadMessages(n: Int): Future[List[Message]] = Future {
    DB.getUnreadMessages(Cached.loginUsername, n).map(MessageMapper.transform)
  }

  override def loadUnreadMessages(friendName: String): Future[List[Message]] = Future {
    DB.getUnreadMessages(Cached.loginUsername, friendName).map(MessageMapper.transform)
  }
}