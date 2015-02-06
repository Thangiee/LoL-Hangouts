package com.thangiee.lolhangouts.data.entities

import java.text.DecimalFormat

import thangiee.riotapi.league.MiniSeries

package object mappers {

  def calculateElo(leagueTier: String, leagueDivision: String, leaguePoints: Int, series: Option[MiniSeries]): Int = {
    val TierWeight = 350.0
    val DivisionWeight = 70.0
    var elo = 450.0

    leagueTier.toUpperCase match {
      case "BRONZE"     => elo += TierWeight * 1
      case "SILVER"     => elo += TierWeight * 2
      case "GOLD"       => elo += TierWeight * 3
      case "PLATINUM"   => elo += TierWeight * 4
      case "DIAMOND"    => elo += TierWeight * 5
      case "MASTER"     => elo += TierWeight * 6
      case "CHALLENGER" => elo += TierWeight * 6
      case _            => elo += TierWeight * 0
    }

    if (!leagueTier.toUpperCase.equals("CHALLENGER"))
      leagueDivision.toUpperCase match {
        case "I"    => elo += DivisionWeight * 4
        case "II"   => elo += DivisionWeight * 3
        case "III"  => elo += DivisionWeight * 2
        case "IV"   => elo += DivisionWeight * 1
        case "V"    => elo += DivisionWeight * 0
        case _      => elo += DivisionWeight * 0
      }

    series.map { s =>
      val n = if (s.target == 3) 5 else 3 // determine 5 or 3 games series
      elo += 20.0 * (s.wins / n)
    }

    elo += leaguePoints * .5
    elo.toInt
  }

  implicit class Rounding(number: Double) {
    def roundTo(DecimalPlace: Int): Double = {
      if (number.isNaN) return 0.0
      new DecimalFormat("###." + ("#" * DecimalPlace)).format(number).toDouble
    }
  }
}
