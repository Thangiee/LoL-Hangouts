package com.thangiee.LoLHangouts.api

import com.thangiee.LoLHangouts.R

sealed trait Region {
  val name: String
  val url: String
  val flag: Int
}

object Region {
  def getFromString(regionName: String): Option[Region] = regionName match {
    case BR.name ⇒ Some(BR)
    case EUNE.name ⇒ Some(EUNE)
    case EUW.name ⇒ Some(EUW)
    case KR.name ⇒ Some(KR)
    case LAN.name ⇒ Some(LAN)
    case LAS.name ⇒ Some(LAS)
    case NA.name ⇒ Some(NA)
    case OCE.name ⇒ Some(OCE)
    case RU.name ⇒ Some(RU)
    case TR.name ⇒ Some(TR)
    case _ ⇒ None
  }
}

object BR extends Region {
  override val url: String = "chat.br.lol.riotgames.com"
  override val name: String = "Brazil"
  override val flag: Int = R.drawable.ic_br

  override def toString: String = "br"
}

object EUNE extends Region {
  override val url: String = "chat.eun1.lol.riotgames.com"
  override val name: String = "Europe Nordic and East"
  override val flag: Int = R.drawable.ic_eune

  override def toString: String = "eune"
}

object EUW extends Region {
  override val url: String = "chat.euw1.lol.riotgames.com"
  override val name: String = "Europe West"
  override val flag: Int = R.drawable.ic_euw

  override def toString: String = "euw"
}

object KR extends Region {
  override val url: String = "chat.kr.lol.riotgames.com"
  override val name: String = "Korea"
  override val flag: Int = R.drawable.ic_south_korea

  override def toString: String = "kr"
}

object LAN extends Region {
  override val url: String = "chat.la1.lol.riotgames.com"
  override val name: String = "Latin America North"
  override val flag: Int = R.drawable.ic_latamn

  override def toString: String = "lan"
}

object LAS extends Region {
  override val url: String = "chat.la2.lol.riotgames.com"
  override val name: String = "Latin America South"
  override val flag: Int = R.drawable.ic_latams

  override def toString: String = "las"
}

object NA extends Region {
  override val url: String = "chat.na1.lol.riotgames.com"
  override val name: String = "North America"
  override val flag: Int = R.drawable.ic_na

  override def toString: String = "na"
}

object OCE extends Region {
  override val url: String = "chat.oc1.lol.riotgames.com"
  override val name: String = "Oceania"
  override val flag: Int = R.drawable.ic_oce

  override def toString: String = "oce"
}

object RU extends Region {
  override val url: String = "chat.ru.lol.riotgames.com"
  override val name: String = "Russia"
  override val flag: Int = R.drawable.ic_ru

  override def toString: String = "ru"
}

object TR extends Region {
  override val url: String = "chat.tr.lol.riotgames.com"
  override val name: String = "Turkey"
  override val flag: Int = R.drawable.ic_tr

  override def toString: String = "tr"
}

