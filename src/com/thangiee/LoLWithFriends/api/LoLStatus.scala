package com.thangiee.LoLWithFriends.api

import scala.xml.XML

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

  def get(summoner: Summoner, value: LoLStatus): String = {
    if (!summoner.isOnline) return ""
    val xml = XML.loadString(summoner.status)
    (xml \\ "body" \\ value.toString).map(_.text).mkString
  }
}
