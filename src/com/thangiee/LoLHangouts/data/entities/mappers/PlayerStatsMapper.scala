package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.PlayerStatsEntity
import com.thangiee.LoLHangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.LoLHangouts.domain.entities.PlayerStats
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.static_data.{Champion, SummonerSpell}

object PlayerStatsMapper {
  implicit val caller = new CachingApiCaller()

  def transform(p: PlayerStatsEntity): PlayerStats = {
    PlayerStats(
      p.playerName,
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
      p.partyId,
      p.series.map(m => m.progress.toCharArray),
      RiotApi.spellStaticDataById(p.spellOneId).getOrElse(SummonerSpell(name = "flash")).name,
      RiotApi.spellStaticDataById(p.spellTwoId).getOrElse(SummonerSpell(name = "ignite")).name
    )
  }

}
