package com.thangiee.lolhangouts.data.usecases.entities

import play.api.libs.json._

case class ScoutReport(
  queueType: String,
  mapName: String,
  blueTeam: List[PlayerStats],
  purpleTeam: List[PlayerStats]
  )

object ScoutReport {
  implicit val scoutReportFmt = Json.reads[ScoutReport]
}

