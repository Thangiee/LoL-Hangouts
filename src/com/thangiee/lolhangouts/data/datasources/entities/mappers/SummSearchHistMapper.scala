package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.SummSearchHistEntity
import com.thangiee.lolhangouts.data.usecases.entities.SummSearchHist

object SummSearchHistMapper {

  def transform(s: SummSearchHistEntity): SummSearchHist = {
    SummSearchHist(s.inGameName, s.regionId, isFriend = false)
  }
}
