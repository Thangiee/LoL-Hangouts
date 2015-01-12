package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.entities.MessageEntity
import com.thangiee.LoLHangouts.data.entities.mappers.MessageMapper
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.data.repository.datasources.sqlite.DB
import com.thangiee.LoLHangouts.domain.entities.Message
import com.thangiee.LoLHangouts.domain.exception.{SendMessageException, GetMessageException}
import com.thangiee.LoLHangouts.domain.repository.MessageRepo

import scala.util.{Failure, Success, Try}

trait MessageRepoImpl extends MessageRepo {

  override def getAllMessages: Either[Exception, List[Message]] = {
    Try {
      DB.getAllMessages
    } match {
      case Success(m) => Right(m.map(MessageMapper.transform))
      case Failure(e) => Left(GetMessageException(e))
    }
  }

  override def deleteAllMessages(friendName: String): Option[Exception] = {
    Try {
      DB.deleteMessages(LoLChat.loginName(), friendName)
    } match {
      case Success(_)  => None
      case Failure(e) => Some(new RuntimeException("Fail to delete all Messages", e))
    }
  }

  override def saveMessages(message: List[Message]): Option[Exception] = {
    Try {
      message.map(m => new MessageEntity(LoLChat.loginName(), m.friendName, m.text, m.isSentByUser, m.isRead, m.date).save())
    } match {
      case Success(_)  => None
      case Failure(e) => Some(new RuntimeException("Fail to save messages", e))
    }
  }

  override def getMessages(friendName: String, n: Int): Either[Exception, List[Message]] = {
    Try {
      DB.getMessages(LoLChat.loginName(), friendName, n)
    } match {
      case Success(m) => Right(m.map(MessageMapper.transform))
      case Failure(e) => Left(GetMessageException(e))
    }
  }

  override def getUnreadMessages(n: Int): Either[Exception, List[Message]] = {
    Try {
      DB.getUnreadMessages(LoLChat.loginName(), n)
    } match {
      case Success(m) => Right(m.map(MessageMapper.transform))
      case Failure(e) => Left(GetMessageException(e))
    }
  }

  override def getUnreadMessages(friendName: String): Either[Exception, List[Message]] = {
    Try {
      DB.getUnreadMessages(LoLChat.loginName(), friendName)
    } match {
      case Success(m) => Right(m.map(MessageMapper.transform))
      case Failure(e) => Left(GetMessageException(e))
    }
  }

  override def getLastMessage(friendName: String): Either[Exception, Option[Message]] = {
    Try {
      DB.getLastMessage(LoLChat.loginName(), friendName)
    } match {
      case Success(m) => Right(m.map(MessageMapper.transform))
      case Failure(e) => Left(GetMessageException(e))
    }
  }

  override def sendMessage(msg: Message): Option[Exception] = {
    LoLChat.getFriendByName(msg.friendName) match {
      case Some(f) =>
        if (LoLChat.sendMessage(f, msg.text)) None
        else Some(SendMessageException("Fail to send message"))
      case None    =>
        Some(SendMessageException(s"Can't send message because ${msg.friendName} is not on the friend list"))
    }
  }

  override def setMessagesRead(friendName: String): Option[Exception] = {
    DB.getUnreadMessages(LoLChat.loginName(), friendName).map(_.setRead(true).save())
    None
  }
}

object MessageRepoImpl extends MessageRepoImpl