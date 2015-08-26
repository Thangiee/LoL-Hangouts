package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolchat.LoLChat
import com.thangiee.lolchat.error.{NoSession, NotFound}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.entities.UserEntity
import com.thangiee.lolhangouts.data.datasources.entities.mappers.UserMapper
import com.thangiee.lolhangouts.data.usecases.GetUserUseCase.{GetUserError, InternalError}
import com.thangiee.lolhangouts.data.usecases.entities.User
import com.thangiee.lolhangouts.data.utils._
import org.scalactic.OptionSugar._
import org.scalactic._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GetUserUseCase extends Interactor {

  def loadUser(): Future[User Or GetUserError]
}

object GetUserUseCase {
  sealed trait GetUserError
  object InternalError extends GetUserError
}

case class GetUserUseCaseImpl() extends GetUserUseCase {

  override def loadUser(): Future[User Or GetUserError] = Future {
    import CacheIn.Memory._

    val res = for {
      sess       <- LoLChat.findSession(Cached.loginUsername)
      regionId   <- Cached.loginRegionId.toOr(NotFound("Cant find cached region id"))
      id         = sess.summId.getOrElse("-1")
      // If the in game name is not cached, we will try to get it from the riot server.
      // And if that fails too, use the username for logging in.
      inGameName = Cached.inGameName(id).getOrElse(summonerNameById(id).getOrElse(sess.user))
    } yield {
      Cached.inGameName_=(id → inGameName) // save it for backup

      UserMapper.transform {
        UserEntity(
          sess.user,
          inGameName,
          regionId,
          Cached.statusMsg(id).getOrElse("Using LoLHangout App"),
          Cached.friendChat(Cached.loginUsername),
          sess.groupNames
        )
      }
    }

    res.badMap {
      case NoSession(msg) => warn(s"[!] $msg"); InternalError
      case NotFound(msg)  => info(s"[-] $msg"); InternalError
    }
  }
}