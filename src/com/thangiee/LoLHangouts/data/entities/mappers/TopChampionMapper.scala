package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.TopChampEntity
import com.thangiee.LoLHangouts.domain.entities.TopChampion

import scala.util.Try

object TopChampionMapper {

  def transform(c: TopChampEntity): TopChampion = {
    TopChampion(
      c.name,
      numOfGames = c.win + c.lose,
      winsRate = Try{ ((c.win.toDouble / (c.win + c.lose)) * 100).roundTo(1) }.getOrElse(0),
      c.avgKills,
      c.avgKillsPerformance,
      c.avgDeaths,
      c.avgDeathsPerformance,
      c.avgAssists,
      c.avgAssistsPerformance,
      c.avgCs,
      c.avgCsPerformance,
      c.avgGold,
      c.avgGoldPerformance,
      c.overAllPerformance
    )
  }
}
