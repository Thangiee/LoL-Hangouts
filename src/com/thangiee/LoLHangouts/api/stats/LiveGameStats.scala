package com.thangiee.LoLHangouts.api.stats

trait LiveGameStats {
  val allPlayers: List[LiveGamePlayerStats]

  val blueTeam: List[LiveGamePlayerStats]

  val purpleTeam: List[LiveGamePlayerStats]

  val queueType: String

  val mapId: Int
}
