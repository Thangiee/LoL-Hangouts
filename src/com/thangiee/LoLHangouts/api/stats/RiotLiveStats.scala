package com.thangiee.LoLHangouts.api.stats

import com.thangiee.LoLHangouts.api.utils.{RiotApi, ChampionIdMap}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scalaj.http.{Http, HttpOptions}

class RiotLiveStats(playerName: String, playerRegion: String) extends LiveGameStats {
  RiotApi.setRegion(playerRegion)
  private val (teams, selections) = fetchData()
  override val blueTeam: List[LiveGamePlayerStats] = teams._1.map(js => createPlayer(js))
  override val purpleTeam: List[LiveGamePlayerStats] = teams._2.map(js => createPlayer(js))
  override val allPlayers: List[LiveGamePlayerStats] = blueTeam ++ purpleTeam
  private val allChampSelected = selections.map(js ⇒ parseSelection(js))
  private val allLeagues = RiotApi.getLeagueEntries(allPlayers.map(p ⇒ p.id.toString))

  private def fetchData(): ((List[JsValue], List[JsValue]), List[JsValue]) = {
    val url = "https://community-league-of-legends.p.mashape.com/api/v1.0/" + playerRegion + "/summoner/retrieveInProgressSpectatorGameInfo/" + playerName.replace(" ", "")

    val request = Try(Http(url).header("X-Mashape-Key", "9E70HAYuX3mshyv33NLXXPGN8RoOp1xCewYjsng28cwtKwt3LX")
      .option(HttpOptions.connTimeout(5000))
      .option(HttpOptions.readTimeout(5000)))

    request match {
      case Success(response) ⇒ // got response
        (Json.parse(response.asString) \ "game").asOpt[JsValue] match {
          case Some(value) ⇒ // go through the json
            (((value \ "teamOne" \ "array").as[List[JsValue]], (value \ "teamTwo" \ "array").as[List[JsValue]]), // find the teams
              (value \ "playerChampionSelections" \ "array").as[List[JsValue]]) // find all champions selected

          case None ⇒ throw new IllegalStateException("%s is not in a game or the game has not started.".format(playerName)) // did not find game
        }
      case Failure(e) ⇒ throw e // no response
    }
  }

  private def parseSelection(json: JsValue): ChampSelection = {
    ((JsPath \ "championId").read[Int] and
      (JsPath \ "summonerInternalName").read[String] and
      (JsPath \ "spell1Id").read[Int] and
      (JsPath \ "spell2Id").read[Int])(ChampSelection.apply _)
      .reads(json)
      .getOrElse(throw new IllegalArgumentException("Json value does not conform to RiotLiveStats#ChampSelection"))
  }

  private def createPlayer(json: JsValue): Player = {
    val info = ((JsPath \ "summonerId").read[Long] and
      (JsPath \ "summonerInternalName").read[String] and
      (JsPath \ "summonerName").read[String])(BasicInfo.apply _)
      .reads(json)
      .getOrElse(throw new IllegalArgumentException("Json value does not conform to RiotLiveStats#BasicInfo"))

    new Player(info)
  }

  // =================
  //   INNER CLASS
  // =================
  private class Player(info: BasicInfo) extends LiveGamePlayerStats {
    private val s4 = RiotApi.getRankedStats(info.summonerId, 4)  // get season4 stats
    private val normal = RiotApi.getNormalStats(info.summonerId, 4)   // get normal game stats
    private lazy val league = allLeagues.get.get(info.summonerId.toString).head  // get the league info of the current player

    override val id: Long = info.summonerId
    override val previousLeagueTier: String = ""
    override val normal5v5: GameModeStats = normal match {
      case Some(stats) ⇒ GameModeStats(stats.getWins, 0, 0, 0, 0, 0)
      case None ⇒ GameModeStats(0, 0, 0, 0, 0, 0)
    }
    override val name: String = info.summonerName
    override val soloQueue: GameModeStats = s4 match {
      case Some(s)  ⇒ val stats = s.getChampions.find(c ⇒ c.getId == 0).get.getStats
                      GameModeStats(stats.getTotalSessionsWon, stats.getTotalSessionsLost,
                                  stats.getTotalChampionKills, stats.getTotalDeathsPerSession,
                                  stats.getTotalAssists, stats.getTotalSessionsPlayed)
      case None     ⇒ GameModeStats(0, 0, 0, 0, 0, 1)
    }
    override lazy val leaguePoints: String = Try(league.getEntries.head.getLeaguePoints + " LP").getOrElse("0 LP")
    override lazy val chosenChamp: String = ChampionIdMap.getName(
      allChampSelected.find {
        i ⇒ i.summonerInternalName.equals(info.summonerInternalName)
      }.get.championId
    )
    override lazy val leagueTier: String = Try(league.getTier).getOrElse("UNRANKED")
    override lazy val leagueDivision: String = Try(league.getEntries.head.getDivision).getOrElse("")
    override lazy val series: Option[Series] = {
      val series = Try(league.getEntries.head.getMiniSeries.getProgress.map {
        case 'W' ⇒ 1
        case 'L' ⇒ -1
        case _ ⇒ 0
      }.toList)

      if (series.isSuccess)
        Some(Series(series.get))
      else None
    }
  }

  private case class BasicInfo(summonerId: Long, summonerInternalName: String, summonerName: String)

  private case class ChampSelection(championId: Int, summonerInternalName: String, spell1Id: Int, spell2Id: Int)
}