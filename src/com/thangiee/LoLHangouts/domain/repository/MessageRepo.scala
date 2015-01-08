package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.Message

trait MessageRepo {

  def sendMessage(msg: Message): Option[Exception]  // todo: move to service class?

  def setMessagesRead(friendName: String): Option[Exception]  // todo: move to service class?

  def getAllMessages: Either[Exception, List[Message]]

  def getMessages(friendName: String, n: Int = Int.MaxValue): Either[Exception, List[Message]]

  def getLastMessage(friendName: String): Either[Exception, Option[Message]]

  def getUnreadMessages(n: Int = Int.MaxValue): Either[Exception, List[Message]]

  def getUnreadMessages(friendName: String): Either[Exception, List[Message]]

  def deleteAllMessages(friendName: String): Option[Exception]

  def saveMessages(messages: List[Message]): Option[Exception]
}
