package com.thangiee.lolhangouts.data.datasources.api

import java.net.{SocketTimeoutException, UnknownHostException}

import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.cache.CanCache
import com.thangiee.lolhangouts.data.usecases.entities.{Champion, Summoner, ProfileSummary, ScoutReport}
import com.thangiee.lolhangouts.data.utils.{TagUtil, _}
import org.scalactic.{Bad, Good, Or}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scalaj.http.Http

trait RiotApiCaller extends AnyRef with TagUtil {

  private val baseUrl = "https://riotapi-microservice.herokuapp.com"

  private def cachedRegionId = Cached.loginRegionId.getOrElse("na")

  def summonerByName(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): Summoner Or RiotError = {
    val url = s"$baseUrl/api/summoner/by-name/?name=$name&region=$regionId"
    call[Summoner](url, 1.hour)
  }

  def summonerById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): Summoner Or RiotError = {
    val url = s"$baseUrl/api/summoner/by-id/?id=$id&region=$regionId"
    call[Summoner](url, 1.hour)
  }

  def summonerNameById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): String Or RiotError = {
    val url = s"$baseUrl/api/summoner/by-id/?id=$id&region=$regionId"
    call[String](url, 1.hour)
  }

  def profileSummaryByName(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): ProfileSummary Or RiotError = {
    val url = s"$baseUrl/api/profile/summary/?name=$name&region=$regionId"
    call[ProfileSummary](url, 1.hour)
  }

  def scoutGame(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): ScoutReport Or RiotError = {
    val url = s"$baseUrl/api/scout/game/?name=$name&region=$regionId"
    call[ScoutReport](url, 20.minutes)
  }

  def champStaticDataById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): Champion Or RiotError = {
    val url = s"$baseUrl/api/static-data/champion/?id=$id&region=$regionId"
    call[Champion](url, 1.hour)
  }

  private def call[T: Reads](url: String, ttl: Duration)(implicit canCache: CanCache[String]): T Or RiotError = {
    val formattedUrl = url.replace(" ", "").toLowerCase

    canCache.get(formattedUrl) match { // blocking
      case Some(cacheHit) => Thread.sleep(100); Good(Json.parse(cacheHit).as[T])
      case None           => // cache missed

        Try(Http(formattedUrl).timeout(10000, 10000).header("authorization", "secret-key").asString) match {
          case Success(response) =>
            debug(s"API call response code: ${response.code} ($formattedUrl)")
            response.code match {
              case 200           =>
                canCache.put(formattedUrl → response.body, Some(ttl)) // cache the response's content
                Good(Json.parse(response.body).as[T]) // and return it
              case 400 => Bad(BadRequest(formattedUrl))
              case 401 => Bad(Unauthorized)
              case 404 => Bad(DataNotFound)
              case 426 => Bad(NeedToUpdate)
              case 422 => Bad(DataNotFound)
              case 429 => Bad(RateLimit)
              case 500 => Bad(ServerError)
              case 503 => Bad(ServiceUnavailable)
            }
          case Failure(e: SocketTimeoutException) => Bad(TimeOut)
          case Failure(e: UnknownHostException)   => Bad(BadRequest(formattedUrl))
          case Failure(e: Throwable)              => e.printStackTrace(); Bad(ServiceUnavailable)
        }

    }
  }

  sealed trait RiotError { def code: Int }
  case class BadRequest(url: String)    extends RiotError { def code: Int = 400 }
  object Unauthorized                   extends RiotError { def code: Int = 401 }
  object NeedToUpdate                   extends RiotError { def code: Int = 426 }
  object RateLimit                      extends RiotError { def code: Int = 429 }
  object DataNotFound                   extends RiotError { def code: Int = 404 }
  object ServerError                    extends RiotError { def code: Int = 500 }
  object ServiceUnavailable             extends RiotError { def code: Int = 503 }
  object TimeOut                        extends RiotError { def code: Int = 408 }
}

object RiotApiCaller extends RiotApiCaller


