package com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class SpectatorGameInfo
(data: Data
   )

object SpectatorGameInfo {
  implicit val spectatorGameInfoFmt = Json.reads[SpectatorGameInfo]
}
