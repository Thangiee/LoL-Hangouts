package com.thangiee.LoLHangouts.data.repository

import com.thangiee.LoLHangouts.data.entities.ProfileSummaryEntity
import com.thangiee.LoLHangouts.data.entities.mappers.ProfileSummaryMapper
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.domain.entities.ProfileSummary
import com.thangiee.LoLHangouts.domain.repository.ProfileSummaryRepo
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.league.{League, LeagueEntry}

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
}

object ProfileSummaryRepoImpl extends ProfileSummaryRepoImpl
