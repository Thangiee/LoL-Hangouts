package com.thangiee.LoLWithFriends.api

trait Servers {
  val host: String
}

object NA extends Servers {
  override val host: String = "chat.na1.lol.riotgames.com"
}

object EU extends Servers {
  override val host: String = ""
}