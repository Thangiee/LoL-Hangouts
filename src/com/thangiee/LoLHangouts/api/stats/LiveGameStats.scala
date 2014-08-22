package com.thangiee.LoLHangouts.api.stats

trait LiveGameStats {
  def allPlayers: List[LiveGamePlayerStats]

  def teammates: List[LiveGamePlayerStats]

  def opponents: List[LiveGamePlayerStats]
}
