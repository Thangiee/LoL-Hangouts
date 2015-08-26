package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.NoSession
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ChangeUserStatusCase extends Interactor {
  def appearOnline(): Future[Unit]
  def appearAway(): Future[Unit]
  def appearOffline(): Future[Unit]
  def setStatusMsg(msg: String): Future[Unit]
}

case class ChangeUserStatusCaseImpl() extends ChangeUserStatusCase {

  override def appearOnline(): Future[Unit] = Future {
    LoLChat.findSession(Cached.loginUsername).map(_.appearOnline()).recover {
      case NoSession(msg) => warn(s"[!] $msg")
    }
  }

  override def appearOffline(): Future[Unit] = Future {
    LoLChat.findSession(Cached.loginUsername).map(_.appearOffline()).recover {
      case NoSession(msg) => warn(s"[!] $msg")
    }
  }

  override def appearAway(): Future[Unit] = Future {
    LoLChat.findSession(Cached.loginUsername).map(_.appearAway()).recover {
      case NoSession(msg) => warn(s"[!] $msg")
    }
  }

  override def setStatusMsg(newMsg: String): Future[Unit] =Future {
    LoLChat.findSession(Cached.loginUsername).map { session =>
      Cached.statusMsg_=(session.summId.getOrElse("") → newMsg)
      session.statusMsg = newMsg
    } recover {
      case NoSession(msg) => warn(s"[!] $msg")
    }
  }
}