package com.thangiee.lolhangouts.data.usecases.entities

import play.api.libs.json._

case class ProfileSummary(
  summonerName: String,
  regionId: String,
  leagueDivision: String,
  leagueName: String,
  leaguePoints: Int,
  leagueTier: String,
  level: Int,
  loses: Int,
  wins: Int,
  games: Int,
  kda: Double,
  killsRatio: Double,
  deathsRatio: Double,
  assistsRatio: Double,
  elo: Int,
  winRate: Double,
  mostPlayedChamps: Seq[MostPlayedChamp]
  )

object ProfileSummary {
  implicit val profileSummaryFmt = Json.reads[ProfileSummary]
}
