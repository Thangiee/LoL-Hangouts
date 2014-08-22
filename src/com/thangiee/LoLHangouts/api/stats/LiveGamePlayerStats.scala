package com.thangiee.LoLHangouts.api.stats

trait LiveGamePlayerStats extends  PlayerStats {

  def chosenChamp: String

  def previousLeagueTier(): String

  def series(): Option[Series]

  case class Series(result: List[Int])
}
