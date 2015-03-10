package com.thangiee.lolhangouts.data.datasources.entities

import thangiee.riotapi.league.MiniSeries

case class GameInfoEntity(queueType: String, mapId: Int, blueTeam: List[PlayerStatsEntity], purpleTeam: List[PlayerStatsEntity])

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