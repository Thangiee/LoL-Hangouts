package com.thangiee.LoLHangouts.domain.entities

case class LiveGame(queueType: String, mapName: String, blueTeam: List[PlayerStats], purpleTeam: List[PlayerStats])

case class PlayerStats
(playerName: String,
 teamNumber: Int,
 regionId: String,
 championName: String,
 leagueTier: String,
 leagueDivision: String,
 leaguePoints: Int,
 rankWins: Int,
 rankLoses: Int,
 normalWin: Int,
 killRatio: Double,
 deathRatio: Double,
 assistRatio: Double,
 elo: Int,
 partyNumber: Option[Int],
 series: Option[Array[Char]],
 spellOne: String,
 spellTwo: String
  )