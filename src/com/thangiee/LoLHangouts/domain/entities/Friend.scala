package com.thangiee.LoLHangouts.domain.entities

import com.thangiee.LoLHangouts.domain.entities.ChatMode.ChatMode
import com.thangiee.LoLHangouts.domain.entities.ChatType.ChatType

case class Friend
(
  name: String,
  id: String,
  chatMode: ChatMode,
  chatType: ChatType,
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

object ChatType extends Enumeration {
  type ChatType = Value
  val Available   = Value("online")
  val Unavailable = Value("offline")
}