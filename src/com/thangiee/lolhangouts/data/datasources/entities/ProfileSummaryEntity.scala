package com.thangiee.lolhangouts.data.datasources.entities

import thangiee.riotapi.league.MiniSeries
import thangiee.riotapi.stats.ChampionStats

case class ProfileSummaryEntity(
  summonerName: String,
  regionId: String,
  leagueDivision: String,
  leagueName: String,
  leaguePoints: Int,
  leagueTier: String,
  level: Int,
  loses: Int,
  wins: Int,
  kills: Int,
  deaths: Int,
  assists: Int,
  games: Int,
  series: Option[MiniSeries],
  champs: Seq[ChampionStats]
  )
