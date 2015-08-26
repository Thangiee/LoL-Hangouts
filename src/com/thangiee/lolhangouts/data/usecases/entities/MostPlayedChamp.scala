package com.thangiee.lolhangouts.data.usecases.entities

import play.api.libs.json._

case class MostPlayedChamp(
  name: String,
  killsRatio: Double,
  deathsRatio: Double,
  assistsRatio: Double,
  games: Int,
  winRate: Double
  )

object MostPlayedChamp {
  implicit val mostPlayedChampFmt = Json.reads[MostPlayedChamp]
}