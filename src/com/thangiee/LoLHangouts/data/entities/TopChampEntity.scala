package com.thangiee.LoLHangouts.data.entities

case class TopChampEntity
(name: String,
 win: Int,
 lose: Int,
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

