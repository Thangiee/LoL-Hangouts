package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.entities.mappers.{MatchMapper, ProfileSummaryMapper, TopChampionMapper}
import com.thangiee.LoLHangouts.data.entities.{MatchEntity, ProfileSummaryEntity, TopChampEntity}
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.domain.entities.{Match, ProfileSummary, TopChampion}
import com.thangiee.LoLHangouts.domain.repository.ProfileDataRepo
import com.thangiee.LoLHangouts.utils.Parser._
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.league.{League, LeagueEntry}

import scala.collection.JavaConversions._

trait ProfileDataRepoImpl extends ProfileDataRepo {
  implicit val caller = new CachingApiCaller()

  override def getSummary(name: String, regionId: String): Either[Exception, ProfileSummary] = {
    for {
      summ      ← RiotApi.summonerByName(name.replace(" ", ""), regionId)
      leagues   ← RiotApi.leagueEntryById(summ.id, regionId)
      rankStats ← RiotApi.rankedStatsById(summ.id, 4, regionId).map(_.getChampions.find(_.id == 0).head.stats.data2)
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

  override def getTopChampions(name: String, regionId: String): Either[Exception, List[TopChampion]] = {
    val url = s"http://www.lolskill.net/summoner/$regionId/${name.replace(" ", "")}/champions"
    val doc = fetchDocument(url)

    doc.map { doc =>
      // check for "Champion Performance" button since the button is hidden and
      // the page is redirected to the summary page for summoner without any top ranked champion.
      // So just return an empty list.
      if (!doc.div("pagination").text().contains("Champion Performance")) return Right(Nil)
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

  override def getMatchHistory(name: String, regionId: String): Either[Exception, List[Match]] = {
    val url = s"http://www.lolskill.net/summoner/$regionId/${name.replace(" ", "")}/matches"
    val fetchDoc = fetchDocument(url)

    fetchDoc map { doc =>
      if (!doc.div("pagination").text().contains("Match History")) return Right(Nil)
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

object ProfileDataRepoImpl extends ProfileDataRepoImpl
