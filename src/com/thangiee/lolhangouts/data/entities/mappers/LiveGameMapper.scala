package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.LiveGameEntity
import com.thangiee.lolhangouts.domain.entities.LiveGame

object LiveGameMapper {

  val mapNames = Map[Int, String](
    1 → "Summoner's Rift",
    2 → "Summoner's Rift",
    3 → "The Proving Grounds",
    4 → "Twisted Treeline",
    8 → "The Crystal Scar",
    10 → "Twisted Treeline",
    11 → "Summoner's Rift",
    12 → "Howling Abyss"
  )

  def transform(g: LiveGameEntity): LiveGame = {
    LiveGame(
      g.queueType,
      mapNames.getOrElse(g.mapId, ""),
      g.blueTeam.map(p => PlayerStatsMapper.transform(p, 1)),
      g.purpleTeam.map(p => PlayerStatsMapper.transform(p, 2))
    )
  }
}
