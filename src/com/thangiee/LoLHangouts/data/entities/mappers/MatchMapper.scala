package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.MatchEntity
import com.thangiee.LoLHangouts.domain.entities.Match

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
