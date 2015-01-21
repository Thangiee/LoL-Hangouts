package com.thangiee.LoLHangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class BannedChampion
(pickTurn: Int,
 championId: Int,
 dataVersion: Int,
 teamId: Int  // 100 or 200
  )

object BannedChampion {
 implicit val bannedChampionFmt = Json.reads[BannedChampion]
}