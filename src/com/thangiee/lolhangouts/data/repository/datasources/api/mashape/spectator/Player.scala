package com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator

import play.api.libs.json._

case class Player
(profileIconId: Int,
 timeAddedToQueue: Long,
 pickTurn: Int,
 summonerId: Long,
 originalAccountNumber: Int,
 summonerName: String,
 accountId: Long,
 summonerInternalName: String,
 teamParticipantId: Option[Long]
  )

object Player {
 implicit val playerFmt = Json.reads[Player]
}