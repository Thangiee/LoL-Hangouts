package com.thangiee.LoLHangouts.api.stats

import com.thangiee.LoLHangouts.api.utils.RiotApi
import com.thangiee.LoLHangouts.api.utils.RiotApi.SummonerSpell
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scalaj.http.{Http, HttpOptions}

class RiotLiveStats(playerName: String, playerRegion: String) extends LiveGameStats {
  RiotApi.setRegion(playerRegion)
  private  val (teams, champSelections, gameInfo)    = fetchData()
  override val blueTeam  : List[LiveGamePlayerStats] = teams._1.map(js => createPlayer(js))
  override val purpleTeam: List[LiveGamePlayerStats] = teams._2.map(js => createPlayer(js))
  override val allPlayers: List[LiveGamePlayerStats] = blueTeam ++ purpleTeam
  override val mapId     : Int                       = gameInfo.mapId
  override val queueType : String                    = gameInfo.queueType
  private  val LeagueList                            = RiotApi.getLeagueEntries(allPlayers.map(p ⇒ p.id.toString))

  private def fetchData(): ((List[JsValue], List[JsValue]), List[ChampSelection], GameInfo) = {
    val url = s"https://community-league-of-legends.p.mashape.com/api/v1.0/$playerRegion/summoner/retrieveInProgressSpectatorGameInfo/${playerName.replace(" ", "")}"

    val request = Try(Http(url).header("X-Mashape-Key", "9E70HAYuX3mshyv33NLXXPGN8RoOp1xCewYjsng28cwtKwt3LX")
      .option(HttpOptions.connTimeout(5000))
      .option(HttpOptions.readTimeout(5000)))

    request match {
      case Success(response) ⇒ // got response
        (Json.parse(response.asString.body) \ "game").asOpt[JsValue].map { jValue =>
          (((jValue \ "teamOne" \ "array").as[List[JsValue]], (jValue \ "teamTwo" \ "array").as[List[JsValue]]), // find the teams
            parseSelections((jValue \ "playerChampionSelections" \ "array").as[List[JsValue]]), // find all champions selected
            parseGameInfo(jValue))
        }.getOrElse(throw new IllegalStateException(s"$playerName is not in a game or the game has not started.")) // did not find game
      case Failure(e) ⇒ throw e // no response
    }
  }

  private def parseGameInfo(json: JsValue): GameInfo = {
    ((JsPath \ "queueTypeName").read[String] and
      (JsPath \ "mapId").read[Int])(GameInfo.apply _)
      .reads(json)
      .getOrElse(throw new IllegalArgumentException("Json value does not conform to RiotLiveStats#ChampSelection"))
  }

  private def parseSelections(jsons: List[JsValue]): List[ChampSelection] = {
    jsons.map { json ⇒
      ((JsPath \ "championId").read[Int] and
        (JsPath \ "summonerInternalName").read[String] and
        (JsPath \ "spell1Id").read[Int] and
        (JsPath \ "spell2Id").read[Int])(ChampSelection.apply _)
        .reads(json)
        .getOrElse(throw new IllegalArgumentException("Json value does not conform to RiotLiveStats#ChampSelection"))
    }
  }

  private def createPlayer(json: JsValue): Player = {
    val info = ((JsPath \ "summonerId").read[Long] and
      (JsPath \ "summonerInternalName").read[String] and
      (JsPath \ "summonerName").read[String] and
      (JsPath \ "teamParticipantId").readNullable[Int])(BasicInfo.apply _)
      .reads(json)
      .getOrElse(throw new IllegalArgumentException("Json value does not conform to RiotLiveStats#BasicInfo"))

    new Player(info)
  }

  // =================
  //   INNER CLASS
  // =================
  private class Player(info: BasicInfo) extends LiveGamePlayerStats {
    private      val s4          = RiotApi.getRankedStats(info.summonerId, 4)
    // get season4 stats
    private      val normal      = RiotApi.getNormalStats(info.summonerId, 4)
    // get normal game stats
    private      val chosenChamp = champSelections.find { i ⇒ i.summonerInternalName.equals(info.summonerInternalName)}.get
    private lazy val league      = LeagueList.get.get(info.summonerId.toString).head // get the league info of the current player

    override val id                : Long          = info.summonerId
    override val previousLeagueTier: String        = ""
    override val normal5v5         : GameModeStats = normal.map(stats => GameModeStats(stats.getWins, 0, 0, 0, 0, 0)).getOrElse(GameModeStats(0, 0, 0, 0, 0, 0))
    override val name              : String        = info.summonerName
    override val soloQueue         : GameModeStats = s4.map(_.getChampions.find(_.getId == 0).get.getStats).map { stats =>
      GameModeStats(
        stats.getTotalSessionsWon, stats.getTotalSessionsLost, stats.getTotalChampionKills,
        stats.getTotalDeathsPerSession, stats.getTotalAssists, stats.getTotalSessionsPlayed)
    }.getOrElse(GameModeStats(0, 0, 0, 0, 0, 1))
    override val spellOne          : SummonerSpell = RiotApi.getSpellById(chosenChamp.spell1Id)
    override val spellTwo          : SummonerSpell = RiotApi.getSpellById(chosenChamp.spell2Id)
    override val chosenChampName   : String        = RiotApi.getChampById(chosenChamp.championId).name
    override val teamId            : Option[Int]   = info.teamParticipantId

    // IMPORTANT: accessing the league variable needs to be define as lazy
    override lazy val leaguePoints  : String         = Try(league.getEntries.head.getLeaguePoints + " LP").getOrElse("0 LP")
    override lazy val leagueTier    : String         = Try(league.getTier).getOrElse("UNRANKED")
    override lazy val leagueDivision: String         = Try(league.getEntries.head.getDivision).getOrElse("")
    override lazy val series        : Option[Series] =
      Try(league.getEntries.head.getMiniSeries.getProgress.map {
        case 'W' ⇒ 1
        case 'L' ⇒ -1
        case _ ⇒ 0
      }.toList).map(outcomes => Some(Series(outcomes))).getOrElse(None)
  }

  private case class GameInfo(queueType: String, mapId: Int)

  private case class BasicInfo(summonerId: Long, summonerInternalName: String, summonerName: String, teamParticipantId: Option[Int])

  private case class ChampSelection(championId: Int, summonerInternalName: String, spell1Id: Int, spell2Id: Int)

}