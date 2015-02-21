package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.lolhangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.domain.exception.UseCaseException
import com.thangiee.lolhangouts.domain.exception.UseCaseException.InternalError
import com.thangiee.lolhangouts.domain.utils._
import thangiee.riotapi.core.{RiotException, RiotApi}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AddFriendUseCase extends Interactor {
  def addFriend(name: String): Future[Unit]
}

case class AddFriendUseCaseImpl() extends AddFriendUseCase {

  override def addFriend(name: String): Future[Unit] = Future {
    implicit val caller = new CachingApiCaller()

    RiotApi.summonerByName(name).map { summoner =>
      info(s"[+] friend request sent to $name")
      LoLChat.connection.getRoster.createEntry(s"sum${summoner.id}@pvp.net", name, null)
        .logThenReturn(_ => s"$name added to friend list")
    } recover {
      case e: RiotException => UseCaseException(e.msg, InternalError).logThenThrow.w
    }
  }
}
