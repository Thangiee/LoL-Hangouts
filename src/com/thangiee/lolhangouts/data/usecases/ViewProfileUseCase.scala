package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.entities.mappers.{MatchMapper, ProfileSummaryMapper, TopChampionMapper}
import com.thangiee.lolhangouts.data.datasources.entities.{MatchEntity, ProfileSummaryEntity, TopChampEntity}
import com.thangiee.lolhangouts.data.datasources.cachingApiCaller
import com.thangiee.lolhangouts.data.usecases.entities.{Match, ProfileSummary, TopChampion}
import com.thangiee.lolhangouts.data.exception.DataAccessException
import com.thangiee.lolhangouts.data.exception.DataAccessException._
import com.thangiee.lolhangouts.data.utils.Parser._
import thangiee.riotapi.core.{RiotApi, RiotException}
import thangiee.riotapi.league.{League, LeagueEntry}

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try}

trait ViewProfileUseCase extends Interactor {
  def loadSummary(username: String, regionId: String): Future[ProfileSummary]
  def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]]
  def loadMatchHistory(username: String, regionId: String): Future[List[Match]]
}

case class ViewProfileUseCaseImpl() extends ViewProfileUseCase {

  override def loadSummary(username: String, regionId: String): Future[ProfileSummary] = Future {
    getSummary(username.toLowerCase, regionId.toLowerCase).map {
      _.logThenReturn(_ => "[+] Profile summary loaded")
    } recover {
      case RiotException(msg, RiotException.DataNotFound) =>
        DataAccessException(s"[-] $msg", DataNotFound).logThenThrow.i
      case RiotException(msg, _) =>
        DataAccessException(s"[!] $msg", GetDataError).logThenThrow.w
    } get
  }

  override def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]] = Future {
    getTopChampions(username.toLowerCase, regionId.toLowerCase).map {
      _.logThenReturn(_ => "[+] Top Champions loaded")
    } recover {
      case RiotException(msg, RiotException.DataNotFound) =>
        DataAccessException(s"[-] $msg", DataNotFound).logThenThrow.i
      case RiotException(msg, _) =>
        DataAccessException(s"[!] $msg", GetDataError).logThenThrow.w
    } get
  }

  override def loadMatchHistory(username: String, regionId: String): Future[List[Match]] = Future {
    getMatchHistory(username.toLowerCase, regionId.toLowerCase).map {
      _.logThenReturn(_ => "[+] Match history loaded")
    } recover {
      case RiotException(msg, RiotException.DataNotFound) =>
        DataAccessException(s"[-] $msg", DataNotFound).logThenThrow.i
      case RiotException(msg, _) =>
        DataAccessException(s"[!] $msg", GetDataError).logThenThrow.w
    } get
  }

  private def getSummary(name: String, regionId: String): Try[ProfileSummary] = {
    for {
      summ      ← RiotApi.summonerByName(name.replace(" ", ""), regionId)
      leagues   ← RiotApi.leagueEntryById(summ.id, regionId)
      rankStats ← RiotApi.rankedStatsById(summ.id, 2015, regionId).map(_.getChampions.find(_.id == 0).head.stats.data2)
      league    = leagues.headOption.getOrElse(League(name = "N/A", tier = "Unranked")) // set default values
      entry     = league.entries.headOption.getOrElse(LeagueEntry())
      series    = entry.miniSeries
      level     = summ.summonerLevel.toInt
      wins      = rankStats.totalSessionsWon.getOrElse(0)
      loses     = rankStats.totalSessionsLost.getOrElse(0)
      kills     = rankStats.totalChampionKills.getOrElse(0)
      deaths    = rankStats.totalDeathsPerSession.getOrElse(0)
      assists   = rankStats.totalAssists.getOrElse(0)
      games     = rankStats.totalSessionsPlayed.getOrElse(0)
      double    = rankStats.totalDoubleKills.getOrElse(0)
      triple    = rankStats.totalTripleKills.getOrElse(0)
      quadra    = rankStats.totalQuadraKills.getOrElse(0)
      penda     = rankStats.totalPentaKills.getOrElse(0)
    } yield ProfileSummaryMapper.transform {
      ProfileSummaryEntity(summ.name, regionId, entry.division, league.name, entry.leaguePoints, league.tier,
        level, loses, wins, kills, deaths, assists, games, series, double, triple, quadra, penda)
    }
  }

  private def getTopChampions(name: String, regionId: String): Try[List[TopChampion]] = {
    val url = s"http://www.lolskill.net/summoner/$regionId/${name.replace(" ", "")}/champions"
    val doc = fetchDocument(url)

    doc.map { doc =>
      // check for "Champion Performance" button since the button is hidden and
      // the page is redirected to the summary page for summoner without any top ranked champion.
      // So just return an empty list.
      if (!doc.div("pagination").text().contains("Champion Performance")) return Success(Nil)
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

  private def getMatchHistory(name: String, regionId: String): Try[List[Match]] = {
    val url = s"http://www.lolskill.net/summoner/$regionId/${name.replace(" ", "")}/matches"
    val fetchDoc = fetchDocument(url)

    fetchDoc map { doc =>
      if (!doc.div("pagination").text().contains("Match History")) return Success(Nil)
    }

    fetchDoc.map(_.tableId("matchHistory").tr().filter(_.hasClass("match")).map { row =>
      MatchMapper.transform {
        MatchEntity(
          champName = row.td("champion tooltip").a().attr("href").split("/").last,
          queueType = row.td("queueInfo").div("queue").text(),
          outCome   = row.td("queueInfo").div("outcome").text(),
          date      = row.td("timeInfo tooltip").div("date").text(),
          duration  = row.td("timeInfo tooltip").div("duration").text(),
          avgKills   = getNumber[Double](row.td("kda").table().head.td().get(1).text().split(" ").head).getOrElse(0),
          avgDeaths  = getNumber[Double](row.td("kda").table().head.td().get(3).text().split(" ").head).getOrElse(0),
          avgAssists = getNumber[Double](row.td("kda").table().head.td().get(5).text().split(" ").head).getOrElse(0),
          avgCs      = getNumber[Int](row.td("stats").table().head.td().get(3).text().split(" ").head).getOrElse(0),
          avgGold    = getNumber[Int](row.td("stats").table().head.td().get(5).text().split(" ").head).getOrElse(0),
          avgKillsPerformance   = getNumber[Double](row.td("kda").table().head.td().get(1).text().split(" ").last).getOrElse(0),
          avgDeathsPerformance  = getNumber[Double](row.td("kda").table().head.td().get(3).text().split(" ").last).getOrElse(0),
          avgAssistsPerformance = getNumber[Double](row.td("kda").table().head.td().get(5).text().split(" ").last).getOrElse(0),
          avgCsPerformance      = getNumber[Int](row.td("stats").table().head.td().get(3).text().split(" ").last).getOrElse(0),
          avgGoldPerformance    = getNumber[Int](row.td("stats").table().head.td().get(5).text().split(" ").last).getOrElse(0),
          overAllPerformance    = getNumber[Double](row.td("stats").table().head.td().get(1).text()).getOrElse(0)
        )
      }
    }.toList)
  }
}