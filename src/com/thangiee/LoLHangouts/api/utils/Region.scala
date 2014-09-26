package com.thangiee.LoLHangouts.api.utils

import com.thangiee.LoLHangouts.R

sealed trait Region {
  val name: String
  val url : String
  val flag: Int
  val id  : String
}

object Region {
  def getFromId(regionId: String): Option[Region] = regionId.toLowerCase match {
    case BR.id    => Some(BR)
    case EUNE.id  => Some(EUNE)
    case EUW.id   => Some(EUW)
    case KR.id    => Some(KR)
    case LAN.id   => Some(LAN)
    case LAS.id   => Some(LAS)
    case NA.id    => Some(NA)
    case OCE.id   => Some(OCE)
    case RU.id    => Some(RU)
    case TR.id    => Some(TR)
    case _        =>  None
  }
}

object BR extends Region {
  override val url : String = "chat.br.lol.riotgames.com"
  override val name: String = "Brazil"
  override val flag: Int    = R.drawable.ic_br
  override val id  : String = "br"
}

object EUNE extends Region {
  override val url : String = "chat.eun1.lol.riotgames.com"
  override val name: String = "Europe Nordic and East"
  override val flag: Int    = R.drawable.ic_eune
  override val id  : String = "eune"
}

object EUW extends Region {
  override val url : String = "chat.euw1.lol.riotgames.com"
  override val name: String = "Europe West"
  override val flag: Int    = R.drawable.ic_euw
  override val id  : String = "euw"
}

object KR extends Region {
  override val url : String = "chat.kr.lol.riotgames.com"
  override val name: String = "Korea"
  override val flag: Int    = R.drawable.ic_south_korea
  override val id  : String = "kr"
}

object LAN extends Region {
  override val url : String = "chat.la1.lol.riotgames.com"
  override val name: String = "Latin America North"
  override val flag: Int    = R.drawable.ic_latamn
  override val id  : String = "lan"
}

object LAS extends Region {
  override val url : String = "chat.la2.lol.riotgames.com"
  override val name: String = "Latin America South"
  override val flag: Int    = R.drawable.ic_latams
  override val id  : String = "las"
}

object NA extends Region {
  override val url : String = "chat.na1.lol.riotgames.com"
  override val name: String = "North America"
  override val flag: Int    = R.drawable.ic_na
  override val id  : String = "na"
}

object OCE extends Region {
  override val url : String = "chat.oc1.lol.riotgames.com"
  override val name: String = "Oceania"
  override val flag: Int    = R.drawable.ic_oce
  override val id  : String = "oce"
}

object RU extends Region {
  override val url : String = "chat.ru.lol.riotgames.com"
  override val name: String = "Russia"
  override val flag: Int    = R.drawable.ic_ru
  override val id  : String = "ru"
}

object TR extends Region {
  override val url : String = "chat.tr.lol.riotgames.com"
  override val name: String = "Turkey"
  override val flag: Int    = R.drawable.ic_tr
  override val id  : String = "tr"
}

