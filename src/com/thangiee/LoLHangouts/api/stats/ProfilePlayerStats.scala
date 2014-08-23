package com.thangiee.LoLHangouts.api.stats

trait ProfilePlayerStats extends PlayerStats {

  val level: Int

  val region: String

  val leagueName: String

  val topChampions: List[Champion]

  val matchHistory: List[Match]
}

sealed case class Champion(name: String, iconUrl: String = "", numOfGame: Int, avgStats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
sealed case class Match(champName: String, queueType: String, outCome: String, date: String, duration: String, avgStats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
sealed case class Stats(kills: Double, deaths: Double, assists: Double, cs: Int, gold: Int)
sealed case class AvgBetterStats(performance: Double = 0.0, kills: Double = 0.0, deaths: Double = 0.0, assists: Double = 0.0,cs: Int = 0, gold: Int = 0)


