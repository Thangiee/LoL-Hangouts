package com.thangiee.lolhangouts.data.entities

import thangiee.riotapi.league.MiniSeries

case class LiveGameEntity(queueType: String, mapId: Int, blueTeam: List[PlayerStatsEntity], purpleTeam: List[PlayerStatsEntity])

case class PlayerStatsEntity
(playerName: String,
 regionId: String,
 championId: Int,
 leagueTier: String,
 leagueDivision: String,
 leaguePoints: Int,
 rankWins: Int,
 rankLoses: Int,
 normalWin: Int,
 kills: Int,
 deaths: Int,
 assists: Int,
 rankGames: Int,
 teamId: Long,
 series: Option[MiniSeries],
 spellOneId: Int,
 spellTwoId: Int
  )