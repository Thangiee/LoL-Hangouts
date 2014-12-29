package com.thangiee.LoLHangouts.domain.entities

sealed trait Region {
  val name: String
  val url : String
  val id  : String
}

object Region {
  def getFromId(regionId: String): Region = regionId.toLowerCase match {
    case BR.id    => BR
    case EUNE.id  => EUNE
    case EUW.id   => EUW
    case KR.id    => KR
    case LAN.id   => LAN
    case LAS.id   => LAS
    case NA.id    => NA
    case OCE.id   => OCE
    case RU.id    => RU
    case TR.id    => TR
    case _        => throw new IllegalArgumentException(s"regionId: $regionId does not match any available region.")
  }
}

object BR extends Region {
  override val url : String = "chat.br.lol.riotgames.com"
  override val name: String = "Brazil"
  override val id  : String = "br"
}

object EUNE extends Region {
  override val url : String = "chat.eun1.lol.riotgames.com"
  override val name: String = "Europe Nordic and East"
  override val id  : String = "eune"
}

object EUW extends Region {
  override val url : String = "chat.euw1.lol.riotgames.com"
  override val name: String = "Europe West"
  override val id  : String = "euw"
}

object KR extends Region {
  override val url : String = "chat.kr.lol.riotgames.com"
  override val name: String = "Korea"
  override val id  : String = "kr"
}

object LAN extends Region {
  override val url : String = "chat.la1.lol.riotgames.com"
  override val name: String = "Latin America North"
  override val id  : String = "lan"
}

object LAS extends Region {
  override val url : String = "chat.la2.lol.riotgames.com"
  override val name: String = "Latin America South"
  override val id  : String = "las"
}

object NA extends Region {
  override val url : String = "chat.na2.lol.riotgames.com"
  override val name: String = "North America"
  override val id  : String = "na"
}

object OCE extends Region {
  override val url : String = "chat.oc1.lol.riotgames.com"
  override val name: String = "Oceania"
  override val id  : String = "oce"
}

object RU extends Region {
  override val url : String = "chat.ru.lol.riotgames.com"
  override val name: String = "Russia"
  override val id  : String = "ru"
}

object TR extends Region {
  override val url : String = "chat.tr.lol.riotgames.com"
  override val name: String = "Turkey"
  override val id  : String = "tr"
}

