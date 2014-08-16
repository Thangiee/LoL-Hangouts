package com.thangiee.LoLHangouts.api

trait LoLStatistics {

  def level(): Int

  def win(): Int

  def lose(): Int

  def kda(): String

  def leagueName(): String

  def leagueTier(): String

  def leagueDivision(): String

  def leaguePoints(): String

  def topChampions(): List[Champion]

  def matchHistory(): List[Match]
}

case class Champion(name: String, iconUrl: String = "", numOfGame: Int, avgStats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Match(champName: String, queueType: String, outCome: String, date: String, duration: String, avgStats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Stats(kills: Double, deaths: Double, assists: Double, cs: Int, gold: Int)
case class AvgBetterStats(performance: Double = 0.0, kills: Double = 0.0, deaths: Double = 0.0, assists: Double = 0.0,cs: Int = 0, gold: Int = 0)
