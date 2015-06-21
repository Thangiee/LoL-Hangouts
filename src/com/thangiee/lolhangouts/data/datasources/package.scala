package com.thangiee.lolhangouts.data

import com.thangiee.lolchat.region._
import com.thangiee.lolhangouts.data.datasources.api.CachingApiCaller

package object datasources {
  object Implicit {
    implicit val cachingApiCaller = CachingApiCaller
  }

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
    case _        => NA
  }

}
