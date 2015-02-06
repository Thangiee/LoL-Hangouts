package com.thangiee.lolhangouts.data.entities

case class MatchEntity
(champName: String,
 queueType: String,
 outCome: String,
 date: String,
 duration: String,
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
