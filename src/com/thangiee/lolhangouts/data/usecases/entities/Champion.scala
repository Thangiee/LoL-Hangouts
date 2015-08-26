package com.thangiee.lolhangouts.data.usecases.entities

case class Champion(
  id: Int,
  title: String,
  name: String,
  key: String
  )

import play.api.libs.json._

object Champion {
  implicit val championFmt = Json.reads[Champion]
}
