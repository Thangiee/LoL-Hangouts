package com.thangiee.lolhangouts.data.usecases.entities

import com.thangiee.lolhangouts.data.usecases.entities.ChatMode.ChatMode

case class Friend
(
  name: String,
  id: String,
  regionId: String,
  latestMsg: Option[Message],
  chatMode: ChatMode,
  isOnline: Boolean,
  timeInGame: Long, // in mils, return 0 if not in a game
  championSelect: Option[String],
  gameStatus: String, // inGame, championSelection, inQueue, etc...
  level: String,
  statusMsg: String,
  rankedLeagueTier: String, // bronze, silver, gold, etc...
  rankedLeagueDivision: String,
  rankedLeagueName: String,
  wins: String
  )

object ChatMode extends Enumeration {
  type ChatMode = Value
  val Chat = Value("chat")
  val Away = Value("away")
  val Dnd  = Value("Do not disturb")
}
