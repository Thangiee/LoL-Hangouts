package com.thangiee.LoLHangouts.api.stats

trait LiveGamePlayerStats extends PlayerStats {

  val id: Long

  val chosenChamp: String

  val previousLeagueTier: String

  val series: Option[Series]

  lazy val elo: Int = {
    val TierWeight = 365.0
    val DivisionWeight = 70.0
    var elo = 450.0

    leagueTier.toUpperCase match {
      case "BRONZE" ⇒ elo += TierWeight * 1
      case "SILVER" ⇒ elo += TierWeight * 2
      case "GOLD" ⇒ elo += TierWeight * 3
      case "PLATINUM" ⇒ elo += TierWeight * 4
      case "DIAMOND" ⇒ elo += TierWeight * 5
      case "CHALLENGER" ⇒ elo += TierWeight * 6
      case _ ⇒ elo += TierWeight * 0
    }

    if (!leagueTier.toUpperCase.equals("CHALLENGER"))
      leagueDivision.toUpperCase match {
        case "I" ⇒ elo += DivisionWeight * 4
        case "II" ⇒ elo += DivisionWeight * 3
        case "III" ⇒ elo += DivisionWeight * 2
        case "IV" ⇒ elo += DivisionWeight * 1
        case "V" ⇒ elo += DivisionWeight * 0
        case _ ⇒ elo += DivisionWeight * 0
      }

    if (series.isDefined) {
      val n = series.get.result.size
      val w = series.get.result.foldLeft(0)((total, i) ⇒ if (i == 1) total + 1 else total)
      elo += 20.0 * (w / n)
    }
    elo.toInt
  }

  case class Series(result: List[Int])

}
