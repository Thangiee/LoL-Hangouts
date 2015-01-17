package com.thangiee.LoLHangouts.data.entities

import thangiee.riotapi.league.MiniSeries

case class ProfileSummaryEntity
(summonerName: String,
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
 doubleKills: Int,
 tripleKills: Int,
 quadraKills: Int,
 pentaKills: Int
  )
