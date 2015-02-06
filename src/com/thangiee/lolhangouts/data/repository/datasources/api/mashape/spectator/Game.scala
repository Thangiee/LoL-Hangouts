package com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class Game
(gameType: String,
 queueTypeName: String,
 playerChampionSelections: List[ChampionSelection],
 teamTwo: List[Player],
 id: Long,
 maxNumPlayers: Int,
 roomPassword: String,
 name: String,
 mapId: Int,
 gameState: String,
 bannedChampions: List[BannedChampion],
 teamOne: List[Player],
 gameMode: String
  )

object Game {
 implicit val gameFmt = Json.reads[Game]
}