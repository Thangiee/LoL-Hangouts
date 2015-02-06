package com.thangiee.lolhangouts.data.repository

import play.api.libs.json._
import com.thangiee.lolhangouts.data.entities.mappers.LiveGameMapper
import com.thangiee.lolhangouts.data.entities.{LiveGameEntity, PlayerStatsEntity}
import com.thangiee.lolhangouts.data.repository.datasources.api.CachingApiCaller
import com.thangiee.lolhangouts.data.repository.datasources.api.mashape.spectator.{ChampionSelection, Player, SpectatorGameInfo}
import com.thangiee.lolhangouts.domain.entities.LiveGame
import com.thangiee.lolhangouts.domain.repository.LiveGameRepo
import com.thangiee.lolhangouts.utils._
import thangiee.riotapi.core.RiotApi
import thangiee.riotapi.league.League
import thangiee.riotapi.stats.PlayerStatsSummary
import thangiee.riotapi.stats.aggregatedstats.Data2

import scala.util.{Failure, Success, Try}
import scalaj.http.{Http, HttpOptions}

trait LiveGameRepoImpl extends LiveGameRepo {
  implicit val caller = new CachingApiCaller()

  override def getGame(name: String, regionId: String): Either[Exception, LiveGame] = {
    for {
      gameInfo   ← fetchGameInfo(name, regionId)
      allPlayers = gameInfo.data.game.teamOne ++ gameInfo.data.game.teamTwo
      ranks      = allPlayers.map(p => p.summonerId → getRankStats(p.summonerId, 2015, regionId)).toMap
      normals    = allPlayers.map(p => p.summonerId → getNormalStats(p.summonerId, 2015, regionId)).toMap
      leagues    ← RiotApi.leagueEntryByIds(allPlayers.map(_.summonerId), regionId)
    } yield LiveGameMapper.transform {
      LiveGameEntity(
        gameInfo.data.game.queueTypeName,
        gameInfo.data.game.mapId,
        gameInfo.data.game.teamOne.map { p =>
          createPlayerStatsEntity(
            p, regionId,
            gameInfo.data.game.playerChampionSelections.find(_.summonerInternalName == p.summonerInternalName).get,
            leagues.getOrElse(p.summonerId, Nil).headOption.headOption.getOrElse(League(tier = "UNRANKED")),
            ranks.get(p.summonerId).get,
            normals.get(p.summonerId).get
          )
        },
        gameInfo.data.game.teamTwo.map { p =>
          createPlayerStatsEntity(
            p, regionId,
            gameInfo.data.game.playerChampionSelections.find(_.summonerInternalName == p.summonerInternalName).get,
            leagues.getOrElse(p.summonerId, Nil).headOption.headOption.getOrElse(League(tier = "UNRANKED")),
            ranks.get(p.summonerId).get,
            normals.get(p.summonerId).get
          )
        }
      )
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

  private def fetchGameInfo(name: String, regionId: String): Either[Exception, SpectatorGameInfo] = {
    val url = s"https://spectator-league-of-legends-v1.p.mashape.com/lol/${regionId.toLowerCase}/v1/spectator/by-name/${name.replace(" ", "")}"

    val request = Try(Http(url).header("X-Mashape-Key", "7uHRIbmcapmshQCKPZCBsQozWKRQp1vJz0kjsne9rnsYwwPqLo")
      .option(HttpOptions.connTimeout(5000))
      .option(HttpOptions.readTimeout(5000)))

    request match {
      case Success(response) =>
        response.asString.code match {
          case 200 =>
            Json.parse(response.asString.body).asOpt[SpectatorGameInfo] match {
              case Some(gameInfo) => Right(gameInfo)
              case None           => Left(new IllegalStateException(s"$name is not in a game or it has not started."))
            }
          case 404 => Left(new IllegalStateException(s"$name is not in a game or it has not started."))
          case 500 => Left(new IllegalStateException("Server is not responding"))
        }
      case Failure(e)        => Left(new IllegalStateException("Connection time out"))
    }
  }

  private def createPlayerStatsEntity(p: Player, regionId: String, chosenChamp: ChampionSelection, league: League, rank: Data2, normal: PlayerStatsSummary): PlayerStatsEntity = {
    PlayerStatsEntity(
      playerName = p.summonerName,
      regionId = regionId,
      championId = chosenChamp.championId,
      spellOneId = chosenChamp.spell1Id,
      spellTwoId = chosenChamp.spell2Id,
      partyId = p.teamParticipantId,
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

object LiveGameRepoImpl extends LiveGameRepoImpl
