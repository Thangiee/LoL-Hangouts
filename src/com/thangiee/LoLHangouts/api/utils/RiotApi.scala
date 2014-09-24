package com.thangiee.LoLHangouts.api.utils

import java.net.SocketTimeoutException
import java.util

import android.os.SystemClock
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jriot.main.JRiotException._
import com.jriot.main.{ApiCaller, JRiotException}
import com.jriot.objects._
import com.thangiee.LoLHangouts.api.Keys
import com.thangiee.LoLHangouts.utils.TLogger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

/**
 * This Singleton handle calling the Riot API and caching the the results
 */
object RiotApi extends TLogger {
  private val gson = new Gson()
  private var region_ = "na"

  def baseUrl(): String = "https://" + region_ + ".api.pvp.net/api/lol/" + region_

  def get(cacheKey: String, url: String): Option[String] = {
    val cacheResult = MemCache.get[String](cacheKey)

    if (cacheResult == null) {  // no result cached
      info("[-] cache " + cacheKey + " miss")
      val caller: ApiCaller = new ApiCaller

      for (attempt ← 0 until Keys.keys.size / 2) {
        // do it the hard way then
        val key = if (attempt == 5) Keys.masterKey else Keys.randomKey

        // call the API request
        Try(caller.request(url + key)) match {
          case Success(result) ⇒ // got response
            MemCache.put(cacheKey, result) // cache it
            return Some(result)

          case Failure(error) ⇒ error match {
            // no response, lets find out what why...
            case e: JRiotException ⇒ e.getErrorCode match {
              case ERROR_API_KEY_LIMIT ⇒ warn("[!] API key Limit: " + key.dropRight(8) + "****-****-****-************")
              case ERROR_API_KEY_WRONG ⇒ warn("[!] API key gone bad: " + key.dropRight(8) + "****-****-****-************")
              case ERROR_BAD_REQUEST ⇒ throw ISE("Bad Request")
              case ERROR_INTERNAL_SERVER_ERROR ⇒ throw ISE("There is currently a problem with the server")
              case ERROR_DATA_NOT_FOUND ⇒ return None
              case ERROR_SERVICE_UNAVAILABLE ⇒ throw ISE("Service unavailable")
            }
            case e: SocketTimeoutException ⇒ throw new SocketTimeoutException("Connection time out. Try refreshing.")
            case e: NoSuchElementException ⇒ return None
            case _ ⇒ throw error
          }
        }
        SystemClock.sleep((10 * Keys.keys.size) / 2)  // wait a bit
      }
      throw ISE("Service is currently unavailable. Please try again later!") // used up all attempts
    } else {
      // found cache result
      info("[+] cache " + cacheKey + " hit")
      Some(cacheResult)
    }
  }

  def setRegion(region: String) = region_ = region.toLowerCase

  def getLeagueEntries(ids: List[String]): Option[util.Map[String, util.List[League]]] = {
    val url = baseUrl() + "/v2.4/league/by-summoner/" + ids.mkString(",") + ",/entry" + "?api_key="
    get("leagues-" + ids.mkString("-"), url) match {
      case Some(json) => Some(gson.fromJson(json, new TypeToken[util.Map[String, util.List[League]]](){}.getType))
      case None       => None
    }
  }

  def getRankedStats(id: Long, season: Int): Option[RankedStats] = {
    val url = baseUrl() + "/v1.3/stats/by-summoner/" + id + "/ranked" + "?season=SEASON" + season + "&api_key="
    get("rank-s%d-%d".format(season, id), url) match {
      case Some(json) => Some(gson.fromJson(json, classOf[RankedStats]))
      case None => None
    }
  }

  def getNormalStats(id: Long, season: Int): Option[PlayerStatsSummary] = {
    val url = baseUrl() + "/v1.3/stats/by-summoner/" + id + "/summary" + "?season=SEASON" + season + "&api_key="
    get("normal-" + id, url) match {
      case Some(json) =>
        Some(gson.fromJson(json, classOf[PlayerStatsSummaryList]).getPlayerStatSummaries
          .find(p ⇒ p.getPlayerStatSummaryType.equals("Unranked")).get) // find normal game stats
      case None => None
    }
  }

  def getChampById(id: Int): Champion = {
    val url = "https://na.api.pvp.net/api/lol/static-data/na/v1.2/champion/" + id + "?&api_key="
    get("champion-" + id, url) match {
      case Some(result) =>
        ((JsPath \ "id").asInt and
          (JsPath \ "key").asString and
          (JsPath \ "name").asString and
          (JsPath \ "title").asString
          )(Champion.apply _).reads(result.toJson).get

      case None => Champion(0, "???", "???", "???")
    }
  }

  def getSpellById(id: Int): SummonerSpell = {
    val url = "https://na.api.pvp.net/api/lol/static-data/na/v1.2/summoner-spell/" + id + "?api_key="
    get("summonerSpell-" + id, url) match {
      case Some(result) =>
        ((JsPath \ "id").asInt and
          (JsPath \ "key").asString and
          (JsPath \ "name").asString and
          (JsPath \ "description").asString and
          (JsPath \ "summonerLevel").asInt
          )(SummonerSpell.apply _).reads(result.toJson).get

      case None => SummonerSpell(0, "???", "???", "???", 0)
    }
  }

  def getSummonerName(id: Int): Option[String] = {
    val url = baseUrl() + "/v1.4/summoner/" + id + "/name?api_key="
    get("name-" + id, url) match {
      case Some(result) => (result.toJson \ id.toString).asOpt[String]
      case None => None
    }
  }

  private def ISE(msg: String) = new IllegalStateException(msg)

  case class Champion(id: Int, key: String, name: String, title: String)

  case class SummonerSpell(id: Int, key: String, name: String, description: String, level: Int)

  private implicit class ReadJsPath(jsPath: JsPath) {
    def asInt = jsPath.read[Int]
    def asString = jsPath.read[String]
  }

  private implicit class UrlToJson(url: String) {
    def toJson = Json.parse(url)
  }
}
