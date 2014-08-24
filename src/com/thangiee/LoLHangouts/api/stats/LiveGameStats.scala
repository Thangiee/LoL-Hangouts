package com.thangiee.LoLHangouts.api.stats

trait LiveGameStats {
  val allPlayers: List[LiveGamePlayerStats]

  val teammates: List[LiveGamePlayerStats]

  val opponents: List[LiveGamePlayerStats]
}
