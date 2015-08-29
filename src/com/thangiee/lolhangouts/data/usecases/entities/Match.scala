package com.thangiee.lolhangouts.data.usecases.entities

case class Match(
  champName: String,
  champId: Int,
  queueType: String,
  isWin: Boolean,
  startTime: Long,
  duration: Int,
  cs: Int,
  gold: Int,
  kills: Int,
  deaths: Int,
  assists: Int,
  items1Id: Int,
  items2Id: Int,
  items3Id: Int,
  items4Id: Int,
  items5Id: Int,
  items6Id: Int,
  trinketId: Int
  )

import play.api.libs.json._

object Match {
  implicit val matchFmt = Json.reads[Match]
}