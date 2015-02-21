package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.ProfileSummaryEntity
import com.thangiee.lolhangouts.data.usecases.entities.ProfileSummary

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
      s.kills,
      s.deaths,
      s.assists,
      s.games,
      kda = Try((s.kills + s.assists + 0.0) / s.deaths).getOrElse(s.kills + s.assists + 0.0).roundTo(1),
      killsRatio = ((s.kills + 0.0) / s.games).roundTo(1),
      deathsRatio = ((s.deaths + 0.0) / s.games).roundTo(1),
      assistsRatio = ((s.assists + 0.0) / s.games).roundTo(1),
      elo = calculateElo(s.leagueTier, s.leagueDivision, s.leaguePoints, s.series),
      winRate = (s.wins.toDouble / s.games) * 100,
      s.doubleKills,
      s.tripleKills,
      s.quadraKills,
      s.pentaKills
    )
  }

}
