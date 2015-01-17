package com.thangiee.LoLHangouts.domain.entities

case class ProfileSummary
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
 kda: Double,
 killsRatio: Double,
 deathsRatio: Double,
 assistsRatio: Double,
 elo: Int,
 winRate: Double,
 doubleKills: Int,
 tripleKills: Int,
 quadraKills: Int,
 pentaKills: Int
  )
