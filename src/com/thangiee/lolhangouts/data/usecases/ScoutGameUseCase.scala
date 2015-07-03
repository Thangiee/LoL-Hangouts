package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.Implicit.cachingApiCaller
import com.thangiee.lolhangouts.data.datasources.entities.mappers.GameInfoMapper
import com.thangiee.lolhangouts.data.datasources.entities.{GameInfoEntity, PlayerStatsEntity}
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.GameInfo
import com.thangiee.lolhangouts.data.utils.Implicits.executionContext
import com.thangiee.lolhangouts.data.utils._
import org.scalactic.Or
import org.scalactic.TrySugar._
import play.api.libs.json.JsResultException
import thangiee.riotapi.core.RiotException.DataNotFound
import thangiee.riotapi.core.{RiotApi, RiotException}
import thangiee.riotapi.currentgame.Participant
import thangiee.riotapi.league.League
import thangiee.riotapi.stats.PlayerStatsSummary
import thangiee.riotapi.stats.aggregatedstats.Data2

import scala.collection.parallel._
import scala.concurrent.Future
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.Try

trait ScoutGameUseCase extends Interactor {
  def loadGameInfo(username: String, regionId: String): Future[GameInfo Or GameInfoError]
}

object ScoutGameUseCase {
  sealed trait GameInfoError
  object GameInfoNotFound extends GameInfoError
  object InternalError extends GameInfoError
}

case class ScoutGameUseCaseImpl() extends ScoutGameUseCase {

  override def loadGameInfo(username: String, regionId: String): Future[GameInfo Or GameInfoError] = Future {
    getGame(username, regionId).toOr.map(_.logThenReturn(_ => "[+] Live game loaded successfully")).badMap {
      case RiotException(msg, DataNotFound) => info(s"[-] $msg"); GameInfoNotFound
      case RiotException(msg, _)            => warn(s"[!] $msg"); InternalError
      case e: JsResultException             => e.printStackTrace(); GameInfoNotFound
    }
  }

  private def getGame(name: String, regionId: String): Try[GameInfo] = {
    for {
      id ← RiotApi.summonerByName(name.replace(" ", ""), regionId).map(_.id)
      gameInfo ← RiotApi.currentGameInfoById(id, regionId)
      allPlayers = gameInfo.participants.par // execute in parallel
      _ = allPlayers.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(allPlayers.size)) // set pool size to #players
      ranks = allPlayers.map(p => p.summonerId → getRankStats(p.summonerId, 2015, regionId)).toMap
      normals = allPlayers.map(p => p.summonerId → getNormalStats(p.summonerId, 2015, regionId)).toMap
      leagues ← RiotApi.leagueEntryByIds(allPlayers.map(_.summonerId).toList, regionId)
    } yield {
      val allPlayersStats = allPlayers.map { p =>
        createPlayerStatsEntity(
          p,
          regionId,
          leagues.getOrElse(p.summonerId, Nil).headOption.getOrElse(League(tier = "UNRANKED")),
          ranks.get(p.summonerId).get,
          normals.get(p.summonerId).get
        )
      }

      GameInfoMapper.transform {
        GameInfoEntity(
          gameInfo.gameType,
          gameInfo.mapId.toInt,
          allPlayersStats.filter(_.teamId == 100),
          allPlayersStats.filter(_.teamId == 200)
        )
      }
    }
  }

  private def getRankStats(id: Long, season: Int, regionId: String): Data2 = {
    RiotApi.rankedStatsById(id, season, regionId).map(_.getChampions.find(_.id == 0).head.stats.data2).getOrElse(Data2())
  }

  private def getNormalStats(id: Long, season: Int, regionId: String): PlayerStatsSummary = {
    val statsSummary = RiotApi.summaryStatsById(id, season, regionId)
    Try(statsSummary.get.getPlayerStatSummaries.find(p => p.playerStatSummaryType == "Unranked").get)
      .getOrElse(PlayerStatsSummary())
  }

  private def createPlayerStatsEntity(p: Participant, regionId: String, league: League, rank: Data2, normal: PlayerStatsSummary): PlayerStatsEntity = {
    PlayerStatsEntity(
      playerName = p.summonerName,
      regionId = regionId,
      championId = p.championId.toInt,
      spellOneId = p.spell1Id.toInt,
      spellTwoId = p.spell2Id.toInt,
      teamId = p.teamId.toInt,
      leagueTier = league.tier,
      leagueDivision = Try(league.entries.head.division).getOrElse(""),
      leaguePoints = Try(league.entries.head.leaguePoints).getOrElse(0),
      rankWins = rank.totalSessionsWon.getOrElse(0),
      rankLoses = rank.totalSessionsLost.getOrElse(0),
      normalWin = normal.wins,
      kills = rank.totalChampionKills.getOrElse(0),
      deaths = rank.totalDeathsPerSession.getOrElse(0),
      assists = rank.totalAssists.getOrElse(0),
      rankGames = rank.totalSessionsPlayed.getOrElse(0),
      series = Try(league.entries.head.miniSeries).getOrElse(None)
    )
  }
}