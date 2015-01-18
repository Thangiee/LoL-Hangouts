package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.entities.mappers.{ProfileSummaryMapper, TopChampionMapper}
import com.thangiee.LoLHangouts.data.entities.{ProfileSummaryEntity, TopChampEntity}
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.domain.entities.{ProfileSummary, TopChampion}
import com.thangiee.LoLHangouts.domain.repository.ProfileSummaryRepo
import com.thangiee.LoLHangouts.utils.Parser._
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.league.{League, LeagueEntry}

import scala.collection.JavaConversions._

trait ProfileSummaryRepoImpl extends ProfileSummaryRepo {
  implicit val caller = new CachingApiCaller()

  override def getProfileSummary(name: String, regionId: String): Either[Exception, ProfileSummary] = {
    for {
      summ      ← RiotApi.summonerByName(name, regionId)
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
    val url = s"http://www.lolskill.net/summoner/$regionId/$name/champions"
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
}

object ProfileSummaryRepoImpl extends ProfileSummaryRepoImpl
