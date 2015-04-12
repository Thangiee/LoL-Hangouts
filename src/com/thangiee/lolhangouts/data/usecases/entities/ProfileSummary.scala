package com.thangiee.lolhangouts.data.usecases.entities

case class ProfileSummary(
  summonerName: String,
  regionId: String,
  leagueDivision: String,
  leagueName: String,
  leaguePoints: Int,
  leagueTier: String,
  level: Int,
  loses: Int,
  wins: Int,
  games: Int,
  kda: Double,
  killsRatio: Double,
  deathsRatio: Double,
  assistsRatio: Double,
  elo: Int,
  winRate: Double,
  mostPlayedChamps: Seq[MostPlayedChamp]
  )

case class MostPlayedChamp(name: String, killsRatio: Int, deathsRatio: Int, assistsRatio: Int, games: Int, winRate: Int)
