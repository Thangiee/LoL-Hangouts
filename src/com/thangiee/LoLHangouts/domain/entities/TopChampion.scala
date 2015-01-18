package com.thangiee.LoLHangouts.domain.entities

case class TopChampion
(name: String,
 numOfGames: Int,
 winsRate: Double,
 avgKills: Double,
 avgKillsPerformance: Double,
 avgDeaths: Double,
 avgDeathsPerformance: Double,
 avgAssists: Double,
 avgAssistsPerformance: Double,
 avgCs: Int,
 avgCsPerformance: Int,
 avgGold: Int,
 avgGoldPerformance: Int,
 overAllPerformance: Double = 0.0
  )
