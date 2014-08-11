package com.thangiee.LoLWithFriends.api

import scala.util.Try

trait LoLStatistics {

  def level(): Try[Int]

  def win(): Try[Int]

  def lose(): Try[Int]

  def kda(): Try[String]

  def leagueName(): Try[String]

  def leagueTier(): Try[String]

  def leagueDivision(): Try[String]

  def leaguePoints(): Try[String]

  def topChampions(): List[Try[Champion]]

  def matchHistory(): List[Try[Match]]
}

case class Champion(name: String, iconUrl: String = "", numOfGame: Int, stats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Match(champId: Int, queueType: String, outCome: String, date: String, duration: String, stats: Stats, avgBetterStats: AvgBetterStats = AvgBetterStats())
case class Stats(kills: Double, deaths: Double, assists: Double, cs: Int, gold: Int)
case class AvgBetterStats(performance: Double = 0.0, kill: Double = 0.0, death: Double = 0.0, assists: Double = 0.0,cs: Int = 0, gold: Int = 0)
