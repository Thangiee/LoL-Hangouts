package com.thangiee.LoLHangouts.api.stats

import java.text.DecimalFormat

import scala.util.Try

trait PlayerStats {

   val name: String

   val leagueTier: String

   val leagueDivision: String

   val leaguePoints: String

   val soloQueue: GameModeStats

   val normal5v5: GameModeStats

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
