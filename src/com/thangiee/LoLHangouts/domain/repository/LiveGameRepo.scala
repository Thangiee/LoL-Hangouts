package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.LiveGame

trait LiveGameRepo {
  
  def getGame(name: String, regionId: String): Either[Exception, LiveGame]
}
