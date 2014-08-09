package com.thangiee.LoLWithFriends.api

object LoLStatus extends Enumeration {
  type LoLStatus = Value
  val ProfileIcon = Value("profileIcon")
  val Level = Value("level")
  val Wins = Value("wins")
  val Leaves = Value("leaves")
  val OdinWins = Value("odinWins")
  val OdinLeaves = Value("odinLeaves")
  val RankedLosses = Value("rankedLosses")
  val RankedRating = Value("rankedRating")
  val Tier = Value("tier")
  val StatusMsg = Value("statusMsg")
  val GameQueueType = Value("gameQueueType")
  val TimeStamp = Value("timeStamp")
  val GameStatus = Value("gameStatus")
  val RankedLeagueName = Value("rankedLeagueName")
  val SkinName = Value("skinname")
  val RankedLeagueDivision = Value("rankedLeagueDivision")
  val RankedLeagueTier = Value("rankedLeagueTier")
  val RankedLeagueQueue = Value("rankedLeagueQueue")
  val RankedWins = Value("rankedWins")

  def parse(summoner: Summoner, value: LoLStatus): Option[String] = {
    if (!summoner.isOnline) return None
    if (summoner.status == null) return None
    val pattern = "(?<=" + value.toString + ">).*?(?=</" + value.toString + ")"
    val result = pattern.r.findFirstIn(summoner.status).getOrElse("")
    if (!result.isEmpty) Some(result) else None
  }
}
