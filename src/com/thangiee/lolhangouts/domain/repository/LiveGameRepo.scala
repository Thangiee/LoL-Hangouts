package com.thangiee.lolhangouts.domain.repository

import com.thangiee.lolhangouts.domain.entities.LiveGame

trait LiveGameRepo {
  
  def getGame(name: String, regionId: String): Either[Exception, LiveGame]
}
