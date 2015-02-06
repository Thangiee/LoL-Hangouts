package com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class Data
(game: Game,
  gameName: String
   )

object Data {
  implicit val dataInfoFmt = Json.reads[Data]
}
