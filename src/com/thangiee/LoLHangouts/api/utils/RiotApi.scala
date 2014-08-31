package com.thangiee.LoLHangouts.api.utils

import java.net.SocketTimeoutException
import java.util

import android.os.SystemClock
import com.jriot.main.JRiotException._
import com.jriot.main.{JRiot, JRiotException}
import com.jriot.objects.{League, PlayerStatsSummary, RankedStats}
import com.thangiee.LoLHangouts.api.Keys
import com.thangiee.LoLHangouts.utils.{CacheUtils, TLogger}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

/**
 *  This Singleton handle calling the Riot API and caching the the results
 */
object RiotApi extends TLogger {
  private val jRiot = new JRiot()

  def get[T <: AnyRef](cacheKey: String, function: ⇒ T): Option[T] = {
    val cacheResult = CacheUtils.get[T](cacheKey)

    if (cacheResult == null) {  // no result cached
      info("[-] cache " + cacheKey + " miss")
      for (attempt ← 0 until Keys.keys.size / 2) {  // do it the hard way then
        val key = Keys.randomKey
        jRiot.setApiKey(key)

        // execute the request to the API
        Try(function) match {
          case Success(result) ⇒             // got response
            CacheUtils.put(cacheKey, result) // cache it
            return Some(result)

          case Failure(error) ⇒ error match { // no response, lets find out what why...
            case e: JRiotException ⇒ e.getErrorCode match {
              case ERROR_API_KEY_LIMIT          ⇒ jRiot.setApiKey(if (attempt == 5) Keys.masterKey else key)
              case ERROR_API_KEY_WRONG          ⇒ info("[!] API key gone bad: " + key.dropRight(8) + "****-****-****-************")
              case ERROR_BAD_REQUEST            ⇒ throw ISE("Bad Request")
              case ERROR_INTERNAL_SERVER_ERROR  ⇒ throw ISE("There is currently a problem with the server")
              case ERROR_DATA_NOT_FOUND         ⇒ return None
              case ERROR_SERVICE_UNAVAILABLE    ⇒ throw ISE("Service unavailable")
            }
            case e: SocketTimeoutException ⇒ throw new SocketTimeoutException("Connection time out. Try refreshing.")
            case _ ⇒ warn(error.getMessage)
          }
        }
        SystemClock.sleep((10 * Keys.keys.size) / 2)
      }
      throw ISE("Service is currently unavailable. Please try again later!")
    } else {  // found cache result
      info("[+] cache " + cacheKey + " hit")
      Some(cacheResult)
    }
  }

  def setRegion(region: String): Unit = {
    jRiot.setRegion(region.toLowerCase)
  }

  def getLeagueEntries(ids: List[String]): Option[util.Map[String, util.List[League]]] = {
    get("leagues-" + ids.mkString("-"), jRiot.getLeagueEntries(ids))
  }

  def getRankedStats(id: Long, season: Int): Option[RankedStats] = {
    get("s%d-%d".format(season, id), jRiot.getRankedStats(id, season))
  }

  def getNormalStats(id: Long, season: Int): Option[PlayerStatsSummary] = {
    get("normal-" + id, jRiot.getPlayerStatsSummaryList(id, season)
            .getPlayerStatSummaries.find(p ⇒ p.getPlayerStatSummaryType.equals("Unranked")).get)
  }

  private def ISE(msg: String) = new IllegalStateException(msg)
}
