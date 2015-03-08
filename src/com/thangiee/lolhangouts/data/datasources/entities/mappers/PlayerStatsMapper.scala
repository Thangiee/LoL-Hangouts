package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.cachingApiCaller
import com.thangiee.lolhangouts.data.datasources.entities.PlayerStatsEntity
import com.thangiee.lolhangouts.data.usecases.entities.PlayerStats
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.static_data.{Champion, SummonerSpell}

object PlayerStatsMapper {

  def transform(p: PlayerStatsEntity, teamNumber: Int): PlayerStats = {
    PlayerStats(
      p.playerName,
      teamNumber,
      p.regionId,
      RiotApi.champStaticDataById(p.championId).getOrElse(Champion(name = "???")).name,
      p.leagueTier,
      p.leagueDivision,
      p.leaguePoints,
      p.rankWins,
      p.rankLoses,
      p.normalWin,
      killRatio = ((p.kills + 0.0) / p.rankGames).roundTo(1),
      deathRatio = ((p.deaths + 0.0) / p.rankGames).roundTo(1),
      assistRatio = ((p.assists + 0.0) / p.rankGames).roundTo(1),
      calculateElo(p.leagueTier, p.leagueDivision, p.leaguePoints, p.series),
      p.series.map(m => m.progress.toCharArray),
      RiotApi.spellStaticDataById(p.spellOneId).getOrElse(SummonerSpell(name = "flash")).name,
      RiotApi.spellStaticDataById(p.spellTwoId).getOrElse(SummonerSpell(name = "ignite")).name
    )
  }

}
