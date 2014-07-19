package com.thangiee.LoLWithFriends.api

trait Server {
  val name: String
  val url: String
}

object BR extends Server {
  override val url: String = "chat.br.lol.riotgames.com"
  override val name: String = "Brazil"
}

object EUNE extends Server {
  override val url: String = "chat.eun1.riotgames.com"
  override val name: String = "Europe Nordic and East"
}

object EUW extends Server {
  override val url: String = "chat.euw1.lol.riotgames.com"
  override val name: String = "Europe West"
}

object KR extends Server {
  override val url: String = "chat.kr.lol.riotgames.com"
  override val name: String = "Korea"
}

object LAN extends Server {
  override val url: String = "chat.la1.lol.riotgames.com"
  override val name: String = "Latin America North"
}

object LAS extends Server {
  override val url: String = "chat.la2.lol.riotgames.com"
  override val name: String = "Latin America South"
}

object NA extends Server {
  override val url: String = "chat.na1.lol.riotgames.com"
  override val name: String = "North America"
}

object OCE extends Server {
  override val url: String = "chat.oc1.lol.riotgames.com"
  override val name: String = "Oceania"
}

object RU extends Server {
  override val url: String = "chat.ru.lol.riotgames.com"
  override val name: String = "Russia"
}

object TR extends Server {
  override val url: String = "chat.tr.lol.riotgames.com"
  override val name: String = "Turkey"
}

