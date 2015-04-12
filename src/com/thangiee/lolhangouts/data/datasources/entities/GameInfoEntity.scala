package com.thangiee.lolhangouts.data.datasources.entities

import thangiee.riotapi.league.MiniSeries

import scala.collection.parallel.immutable.ParSeq

case class GameInfoEntity(queueType: String, mapId: Int, blueTeam: ParSeq[PlayerStatsEntity], purpleTeam: ParSeq[PlayerStatsEntity])

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