package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.MatchEntity
import com.thangiee.lolhangouts.domain.entities.Match

object MatchMapper {

  def transform(m: MatchEntity): Match = {
    Match(
      m.champName,
      m.queueType,
      m.outCome,
      m.date,
      m.duration,
      m.avgKills,
      m.avgKillsPerformance,
      m.avgDeaths,
      m.avgDeathsPerformance,
      m.avgAssists,
      m.avgAssistsPerformance,
      m.avgCs,
      m.avgCsPerformance,
      m.avgGold,
      m.avgGoldPerformance,
      m.overAllPerformance
    )
  }
}