package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.{NoSession, NotConnected, NotFound}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.MessageEntity
import com.thangiee.lolhangouts.data.usecases.SendMsgUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.Message
import com.thangiee.lolhangouts.data.utils._
import org.scalactic.Or

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SendMsgUseCase extends Interactor {
  def sendMessage(message: Message): Future[Unit Or SendMsgError]
}

object SendMsgUseCase {
  sealed trait SendMsgError
  object NoConnection extends SendMsgError
  object EmptyMessage extends SendMsgError
  object FriendNotFound extends SendMsgError
  object InternalError extends SendMsgError
}

case class SendMsgUseCaseImpl() extends SendMsgUseCase {

  override def sendMessage(msg: Message): Future[Unit Or SendMsgError] = Future {
    if (!msg.text.isEmpty) {
      (for {
        sess <- LoLChat.findSession(Cached.loginUsername)
        friend <- sess.findFriendByName(msg.friendName)
        _ <- sess.sendMsg(friend.id, msg.text)
      } yield {
          new MessageEntity(sess.user, msg.friendName, msg.text, msg.isSentByUser, msg.isRead, msg.date).save()
          info("[+] Message sent and saved to database!")
        }).badMap {
        case NoSession(errMsg) => warn(s"[!] $errMsg"); InternalError
        case NotFound(errMsg)  => info(s"[-] $errMsg"); FriendNotFound
        case NotConnected(_)   => info("[-] No connection, can't send the message"); NoConnection
      }
    } else {
      Bad(EmptyMessage)
    }
  }
}