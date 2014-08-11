package com.thangiee.LoLWithFriends.api

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

case class Champion(name: String, iconUrl: String = "", numOfGame: Int, stats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Match(champId: Int, queueType: String, outCome: String, date: String, duration: String, stats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Stats(kills: Double, deaths: Double, assists: Double, cs: Int, gold: Int)
case class AvgBetterStats(performance: Double = 0.0, kill: Double = 0.0, death: Double = 0.0, assists: Double = 0.0,cs: Int = 0, gold: Int = 0)
