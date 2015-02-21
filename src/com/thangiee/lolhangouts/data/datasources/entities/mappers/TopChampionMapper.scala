package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.TopChampEntity
import com.thangiee.lolhangouts.data.usecases.entities.TopChampion

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
