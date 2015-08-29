package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.entities.mappers.TopChampionMapper
import com.thangiee.lolhangouts.data.datasources.entities.TopChampEntity
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.{Match, ProfileSummary, TopChampion}
import com.thangiee.lolhangouts.data.utils.Parser.ParserError
import com.thangiee.lolhangouts.data.utils._
import com.thangiee.lolhangouts.data._
import org.scalactic.Or

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ViewProfileUseCase extends Interactor {
  def loadSummary(username: String, regionId: String): Future[ProfileSummary Or ViewProfileError]
  def loadTopChamps(username: String, regionId: String): Future[List[TopChampion] Or ViewProfileError]
  def loadMatchHistory(username: String, regionId: String): Future[List[Match] Or ViewHistoryError]
}

object ViewProfileUseCase {
  sealed trait ViewProfileError
  object ProfileNotFound extends ViewProfileError

  sealed trait ViewHistoryError
  object HistoryNotFound extends ViewHistoryError

  object OldAppVersion extends ViewHistoryError with ViewProfileError
  object InternalError extends ViewHistoryError with ViewProfileError
}

case class ViewProfileUseCaseImpl() extends ViewProfileUseCase with Parser {
  import CacheIn.Memory._

  override def loadSummary(username: String, regionId: String): Future[ProfileSummary Or ViewProfileError] = Future {
    profileSummaryByName(username, regionId) match {
      case Good(summary)     => info(s"[+] Profile summary loaded");                      Good(summary)
      case Bad(DataNotFound) => info(s"[-] Profile summary data not found");              Bad(ProfileNotFound)
      case Bad(NeedToUpdate) => info(s"[-] Version of api call to backend out of date."); Bad(OldAppVersion)
      case Bad(e: RiotError) => info(s"[!] Riot api error: $e");                          Bad(InternalError)
    }
  }

  override def loadMatchHistory(username: String, regionId: String): Future[List[Match] Or ViewHistoryError] = Future {
    matchHistoryByName(username, regionId) match {
      case Good(matches)     => info(s"[+] Match history loaded");                        Good(matches)
      case Bad(DataNotFound) => info(s"[-] Match history data not found");                Bad(HistoryNotFound)
      case Bad(NeedToUpdate) => info(s"[-] Version of api call to backend out of date."); Bad(OldAppVersion)
      case Bad(e: RiotError) => info(s"[!] Riot api error: $e");                          Bad(InternalError)
    }
  }

  override def loadTopChamps(username: String, regionId: String): Future[List[TopChampion] Or ViewProfileError] =
    getParsedData(topChampions(username.toLowerCase, regionId.toLowerCase), "[+] Top Champions loaded")

  private def getParsedData[A](f: => Or[A, ParserError], successLog: String): Future[A Or ViewProfileError] = Future {
    f.map(_.logThenReturn(_ => successLog)).badMap {
      case Parser.DataNotFound => info("[-] Parser did not find the summoner info"); ProfileNotFound
      case Parser.ServerBusy => info(s"[!] Parser timeout; failed to connect to the server"); InternalError
    }
  }

  private def topChampions(name: String, regionId: String): List[TopChampion] Or ParserError = {
    val url = s"http://www.lolskill.net/summoner/$regionId/${name.replace(" ", "")}/champions"
    val doc = fetchDocument(url)

    doc.map { doc =>
      // check for "Champion Performance" button since the button is hidden and
      // the page is redirected to the summary page for summoner without any top ranked champion.
      // So just return an empty list.
      if (!doc.div("pagination").text().contains("Champion Performance")) return Good(Nil)
    }

    doc.map(_.tableId("championsTable").tr().tail.map { row =>
      TopChampionMapper.transform {
        TopChampEntity(
          name = row.td("left champion tooltip").a().head.text(),
          win  = getNumber[Int](row.td().get(5).text.split(" /").head).getOrElse(0),
          lose = getNumber[Int](row.td().get(5).text.split(" /").last).getOrElse(0),
          avgKills   = getNumber[Double](row.td().get(6).text.split(" /").last).getOrElse(0),
          avgDeaths  = getNumber[Double](row.td().get(7).text()).getOrElse(0),
          avgAssists = getNumber[Double](row.td().get(8).text()).getOrElse(0),
          avgCs      = getNumber[Int](row.td().get(9).text()).getOrElse(0),
          avgGold    = getNumber[Int](row.td().get(10).text()).getOrElse(0),
          avgKillsPerformance   = getNumber[Double](row.td().get(6).span("small").text()).getOrElse(0),
          avgDeathsPerformance  = getNumber[Double](row.td().get(7).span("small").text()).getOrElse(0),
          avgAssistsPerformance = getNumber[Double](row.td().get(8).span("small").text()).getOrElse(0),
          avgCsPerformance      = getNumber[Int](row.td().get(9).span("small").text()).getOrElse(0),
          avgGoldPerformance    = getNumber[Int](row.td().get(10).span("small").text()).getOrElse(0),
          overAllPerformance    = getNumber[Double](row.td().get(4).text()).getOrElse(0)
        )
      }
    }.toList)
  }
}