package com.thangiee.LoLWithFriends.api

import com.thangiee.LoLWithFriends.R

trait Server {
  val name: String
  val url: String
  val flag: Int
}

object BR extends Server {
  override val url: String = "chat.br.lol.riotgames.com"
  override val name: String = "Brazil"
  override val flag: Int = R.drawable.brazil
  override def toString: String = "br"
}

object EUNE extends Server {
  override val url: String = "chat.eun1.riotgames.com"
  override val name: String = "Europe Nordic and East"
  override val flag: Int = R.drawable.europe
  override def toString: String = "eune"
}

object EUW extends Server {
  override val url: String = "chat.euw1.lol.riotgames.com"
  override val name: String = "Europe West"
  override val flag: Int = R.drawable.europe
  override def toString: String = "euw"
}

object KR extends Server {
  override val url: String = "chat.kr.lol.riotgames.com"
  override val name: String = "Korea"
  override val flag: Int = R.drawable.south_korea
  override def toString: String = "kr"
}

object LAN extends Server {
  override val url: String = "chat.la1.lol.riotgames.com"
  override val name: String = "Latin America North"
  override val flag: Int = R.drawable.latin
  override def toString: String = "lan"
}

object LAS extends Server {
  override val url: String = "chat.la2.lol.riotgames.com"
  override val name: String = "Latin America South"
  override val flag: Int = R.drawable.latin
  override def toString: String = "las"
}

object NA extends Server {
  override val url: String = "chat.na1.lol.riotgames.com"
  override val name: String = "North America"
  override val flag: Int = R.drawable.north_america
  override def toString: String = "na"
}

object OCE extends Server {
  override val url: String = "chat.oc1.lol.riotgames.com"
  override val name: String = "Oceania"
  override val flag: Int = R.drawable.australia
  override def toString: String = "oce"
}

object RU extends Server {
  override val url: String = "chat.ru.lol.riotgames.com"
  override val name: String = "Russia"
  override val flag: Int = R.drawable.russia
  override def toString: String = "ru"
}

object TR extends Server {
  override val url: String = "chat.tr.lol.riotgames.com"
  override val name: String = "Turkey"
  override val flag: Int = R.drawable.turkey
  override def toString: String = "tr"
}

