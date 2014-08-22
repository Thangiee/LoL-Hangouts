package com.thangiee.LoLHangouts.api.stats

import java.text.DecimalFormat

import scala.util.Try

trait PlayerStats {

   def name: String

   def leagueTier(): String

   def leagueDivision(): String

   def leaguePoints(): String

   def soloQueue: GameModeStats

   def normal5v5: GameModeStats

   def kda(gameMode: GameModeStats): String = {
     val (k, d, a) = (gameMode.kills, gameMode.deaths, gameMode.assists)
     val kda = Try((k + a) / d).getOrElse(k + a)
     kda.roundTo(1) + " (" + k.roundTo(1) + "/" + d.roundTo(1) + "/" + a.roundTo(1) + ")"
   }

   case class GameModeStats(wins: Int, losses: Int, kills: Double, deaths: Double, assists: Double, gameTotal: Int)

   implicit class Rounding(number: Double) {
     def roundTo(DecimalPlace: Int): Double = {
       if (number.isNaN) return 0.0
       new DecimalFormat("###." + ("#" * DecimalPlace)).format(number).toDouble
     }
   }
 }
