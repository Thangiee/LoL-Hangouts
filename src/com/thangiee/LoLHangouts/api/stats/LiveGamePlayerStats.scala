package com.thangiee.LoLHangouts.api.stats

trait LiveGamePlayerStats extends  PlayerStats {

  val id: Long

  val chosenChamp: String

  val previousLeagueTier: String

  val series: Option[Series]

  case class Series(result: List[Int])
}
