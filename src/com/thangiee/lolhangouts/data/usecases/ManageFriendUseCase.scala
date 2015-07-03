package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.{NoSession, NotConnected}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.Implicit.cachingApiCaller
import com.thangiee.lolhangouts.data.utils._
import thangiee.riotapi.core.RiotApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ManageFriendUseCase extends Interactor {
  def addFriend(name: String): Future[Unit]
  def removeFriend(summId: String): Future[Unit]
}

case class ManageFriendUseCaseImpl() extends ManageFriendUseCase {

  override def addFriend(name: String): Future[Unit] = Future {
    RiotApi.summonerByName(name).map { summoner =>
      LoLChat.findSession(Cached.loginUsername).flatMap { sess =>
        info(s"[+] friend request sent to $name")
        sess.sendFriendRequest(summoner.id.toString)
      } recover {
        case NoSession(msg) => warn(s"[!] $msg")
        case NotConnected(_) => warn("[!] No connection to send friend request")
      }
    }
  }
  
  def removeFriend(summId: String): Future[Unit] = Future {
   LoLChat.findSession(Cached.loginUsername).flatMap { sess =>
     info(s"[+] friend $summId removed")
     sess.removeFriend(summId)
   } recover {
     case NoSession(msg) => warn(s"[!] $msg")
     case NotConnected(_) => warn("[!] No connection. Can't remove friend.")
   }
  }
}
