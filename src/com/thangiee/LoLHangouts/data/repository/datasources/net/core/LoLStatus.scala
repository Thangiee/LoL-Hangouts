package com.thangiee.LoLHangouts.data.repository.datasources.net.core

object LoLStatus extends Enumeration {
  type LoLStatus = Value
  val ProfileIcon          = Value("profileIcon")
  val Level                = Value("level")
  val Wins                 = Value("wins")
  val Leaves               = Value("leaves")
  val OdinWins             = Value("odinWins")
  val OdinLeaves           = Value("odinLeaves")
  val RankedLosses         = Value("rankedLosses")
  val RankedRating         = Value("rankedRating")
  val Tier                 = Value("tier")
  val StatusMsg            = Value("statusMsg")
  val GameQueueType        = Value("gameQueueType")
  val TimeStamp            = Value("timeStamp")
  val GameStatus           = Value("gameStatus")
  val RankedLeagueName     = Value("rankedLeagueName")
  val SkinName             = Value("skinname")    // selected champion
  val RankedLeagueDivision = Value("rankedLeagueDivision") // I, II, III, IV, V
  val RankedLeagueTier     = Value("rankedLeagueTier")  // Bronze, Silver, Gold, etc...
  val RankedLeagueQueue    = Value("rankedLeagueQueue")
  val RankedWins           = Value("rankedWins")

  /**
   * Parse information from friend status.
   * Note that some values are only available use certain circumstances.
   * For example, SkinName is only available when the friend is in a game.
   */
  def parse(friend: Friend, value: LoLStatus): Option[String] = {
    if (!friend.isOnline) return None
    if (friend.status == null) return None
    val pattern = "(?<=" + value.toString + ">).*?(?=</" + value.toString + ")"
    val result = pattern.r.findFirstIn(friend.status).getOrElse("").replace("&apos;", "")
    if (!result.isEmpty) Some(result) else None
  }
}
