package com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class ChampionSelection
(summonerInternalName: String,
 selectedSkinIndex: Int,
 championId: Int,
 spell1Id: Int,
 spell2Id: Int
  )

object ChampionSelection {
  implicit val championSelectionFmt = Json.reads[ChampionSelection]
}