package com.thangiee.lolhangouts.data.usecases.entities

import play.api.libs.json._

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
  series: IndexedSeq[String],
  spellOne: String,
  spellTwo: String
  )

object PlayerStats {
  implicit val playerStatsFmt = Json.reads[PlayerStats]
}
