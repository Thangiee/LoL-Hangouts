package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.ScoutReport
import com.thangiee.lolhangouts.data._
import org.scalactic.Or

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ScoutGameUseCase extends Interactor {
  def scoutGameInfo(username: String, regionId: String): Future[ScoutReport Or GameInfoError]
}

object ScoutGameUseCase {
  sealed trait GameInfoError
  object GameInfoNotFound extends GameInfoError
  object OldAppVersion extends GameInfoError
  object InternalError extends GameInfoError
}

case class ScoutGameUseCaseImpl() extends ScoutGameUseCase {

  override def scoutGameInfo(username: String, regionId: String): Future[ScoutReport Or GameInfoError] = Future {
    import CacheIn.Memory._

    scoutGame(username, regionId).map(_.logThenReturn(_ => "[+] Live game loaded successfully")).badMap {
      case DataNotFound => info(s"[-] GameInfo data not found");                     GameInfoNotFound
      case NeedToUpdate => info(s"[-] Version of api call to backend out of date."); OldAppVersion
      case riotError    => info(s"[!] Riot api error: ${riotError.toString}");       InternalError
    }
  }
}