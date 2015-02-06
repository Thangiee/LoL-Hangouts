package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.PlayerStatsEntity
import com.thangiee.lolhangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.lolhangouts.domain.entities.PlayerStats
import com.thangiee.lolhangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.static_data.{Champion, SummonerSpell}

object PlayerStatsMapper {
  implicit val caller = new CachingApiCaller()

  def transform(p: PlayerStatsEntity, teamNumber: Int, preMadeParties: Map[Long, Int]): PlayerStats = {
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
      preMadeParties.get(p.partyId.getOrElse(-1)),
      p.series.map(m => m.progress.toCharArray),
      RiotApi.spellStaticDataById(p.spellOneId).getOrElse(SummonerSpell(name = "flash")).name,
      RiotApi.spellStaticDataById(p.spellTwoId).getOrElse(SummonerSpell(name = "ignite")).name
    )
  }

}
