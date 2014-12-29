package com.thangiee.LoLHangouts.domain.entities

import com.thangiee.LoLHangouts.domain.entities.ChatMode.ChatMode
import com.thangiee.LoLHangouts.domain.entities.ChatType.ChatType

class Friend(
            name: String,
            id: String,
            chatMode: ChatMode,
            chatType: ChatType,
            isOnline: Boolean
              )

object ChatMode extends Enumeration {
  type ChatMode = Value
  val Chat = Value("chat")
  val Away = Value("away")
  val Dnd = Value("Do not disturb")
}

object ChatType extends Enumeration {
  type ChatType = Value
  val Available = Value("online")
  val Unavailable = Value("offline")
}