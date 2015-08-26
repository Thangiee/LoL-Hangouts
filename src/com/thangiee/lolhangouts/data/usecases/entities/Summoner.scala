package com.thangiee.lolhangouts.data.usecases.entities

case class Summoner(
  id: Long,
  name: String,
  profileIconId: Int,
  revisionDate: Long,
  summonerLevel: Int
  )

import play.api.libs.json._

object Summoner {
  implicit val summonerFmt = Json.reads[Summoner]
}