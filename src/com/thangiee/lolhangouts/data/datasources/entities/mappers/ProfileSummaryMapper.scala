package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.ProfileSummaryEntity
import com.thangiee.lolhangouts.data.usecases.entities.{MostPlayedChamp, ProfileSummary}
import com.thangiee.lolhangouts.data.datasources.cachingApiCaller
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.static_data.Champion
import thangiee.riotapi.stats.ChampionStats

import scala.util.Try

object ProfileSummaryMapper {

  def transform(s: ProfileSummaryEntity): ProfileSummary = {
    ProfileSummary(
      s.summonerName,
      s.regionId,
      s.leagueDivision,
      s.leagueName,
      s.leaguePoints,
      s.leagueTier,
      s.level,
      s.loses,
      s.wins,
      s.games,
      kda = Try((s.kills + s.assists + 0.0) / s.deaths).getOrElse(s.kills + s.assists + 0.0).roundTo(1),
      killsRatio = ((s.kills + 0.0) / s.games).roundTo(1),
      deathsRatio = ((s.deaths + 0.0) / s.games).roundTo(1),
      assistsRatio = ((s.assists + 0.0) / s.games).roundTo(1),
      elo = calculateElo(s.leagueTier, s.leagueDivision, s.leaguePoints, s.series),
      winRate = ((s.wins.toDouble / s.games) * 100).roundTo(1),
      mostPlayedChamps = s.champs.filter(_.id != 0)  // filter out id 0 since that's the stats of all champs combined
        .sortBy(_.stats.data2.totalSessionsPlayed).reverse.take(4) // sort by most games and take the top 4
        .map(createMostPlayedChamp)
    )
  }

  private def createMostPlayedChamp(c: ChampionStats): MostPlayedChamp = {
    val d = c.stats.data2
    (d.totalChampionKills, d.totalDeathsPerSession, d.totalAssists, d.totalSessionsPlayed, d.totalSessionsWon) match {
      case (Some(kills), Some(deaths), Some(assists), Some(games), Some(wins)) => // lift Option
        MostPlayedChamp(
          name = RiotApi.champStaticDataById(c.id).getOrElse(Champion(name = "")).name,
          killsRatio = ((kills + 0.0) / games).roundTo(1).toInt,
          deathsRatio = ((deaths + 0.0) / games).roundTo(1).toInt,
          assistsRatio = ((assists + 0.0) / games).roundTo(1).toInt,
          games,
          ((wins.toDouble / games) * 100).roundTo(1).toInt
        )
      case _ =>
        MostPlayedChamp("", 0, 0, 0, 0, 0)
    }
  }

}
