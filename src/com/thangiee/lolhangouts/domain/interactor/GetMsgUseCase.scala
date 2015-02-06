package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Message

import scala.concurrent.Future

trait GetMsgUseCase extends Interactor {

  def loadAllMessages: Future[List[Message]]

  def loadMessages(friendName: String, n: Int = Integer.MAX_VALUE): Future[List[Message]]

  def loadUnreadMessages(n: Int): Future[List[Message]]

  def loadUnreadMessages(friendName: String): Future[List[Message]]

  def loadLastMessage(friendName: String): Future[Option[Message]]
}
