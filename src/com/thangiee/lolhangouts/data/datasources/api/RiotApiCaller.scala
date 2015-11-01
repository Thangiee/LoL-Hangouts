package com.thangiee.lolhangouts.data.datasources.api

import java.net.{SocketTimeoutException, UnknownHostException}

import com.thangiee.lolhangouts.data.{Cached, _}
import com.thangiee.lolhangouts.data.datasources.cache.CanCache
import com.thangiee.lolhangouts.data.usecases.entities._
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
    call[Summoner](url, ttl = 1.hour, ver = 1)
  }

  def summonerById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): Summoner Or RiotError = {
    val url = s"$baseUrl/api/summoner/by-id/?id=$id&region=$regionId"
    call[Summoner](url, ttl = 1.hour, ver = 1)
  }

  def summonerNameById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): String Or RiotError = {
    val url = s"$baseUrl/api/summoner/name/by-id/?id=$id&region=$regionId"
    call[String](url, ttl = 1.hour, ver = 1)
  }

  def profileSummaryByName(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): ProfileSummary Or RiotError = {
    val url = s"$baseUrl/api/profile/summary/?name=$name&region=$regionId"
    call[ProfileSummary](url, ttl = 1.hour, ver = 1)
  }

  def matchHistoryByName(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): List[Match] Or RiotError = {
    val url = s"$baseUrl/api/profile/match-history/?name=$name&region=$regionId"
    call[List[Match]](url, ttl = 20.minutes, ver = 1)
  }

  def scoutGame(name: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): ScoutReport Or RiotError = {
    val url = s"$baseUrl/api/scout/game/?name=$name&region=$regionId"
    call[ScoutReport](url, ttl = 20.minutes, ver = 1)
  }

  def champStaticDataById(id: String, regionId: String = cachedRegionId)(implicit c: CanCache[String]): Champion Or RiotError = {
    val url = s"$baseUrl/api/static-data/champion/?id=$id&region=$regionId"
    call[Champion](url, ttl = 1.hour, ver = 1)
  }

  private def call[T: Reads](url: String, ttl: Duration, ver: Int)(implicit canCache: CanCache[String]): T Or RiotError = {
    val formattedUrl = url.replace(" ", "").toLowerCase

    canCache.get(formattedUrl) match {
      case Some(cacheHit) => Thread.sleep(100); Good(Json.parse(cacheHit).as[T])
      case None           => // cache missed
        val headers = Map("Authorization" → "secret-key", "Version" → ver.toString)
        Try(Http(formattedUrl).timeout(connTimeoutMs = 10000, readTimeoutMs = 10000).headers(headers).asString) match {
          case Success(response) =>
            debug(s"API call response code: ${response.code} ($formattedUrl)")
            response.code match {
              case 200           =>
                canCache.put(formattedUrl → response.body, Some(ttl)) // cache the response's content
                warn(response.body)
                Try(Json.parse(response.body).as[T]) match { // check for JsResultException
                  case Success(t) => Good(t)
                  case Failure(e) => e.printStackTrace(); Bad(NeedToUpdate)
                }
              case 400 => Bad(BadRequest(formattedUrl))
              case 401 => Bad(Unauthorized)
              case 404 => Bad(DataNotFound)
              case 426 =>
                val newestVer = response.headers.getOrElse("Version", "???")
                warn(s"Server no longer support version $ver (newest version is $newestVer) for $formattedUrl")
                Bad(NeedToUpdate)
              case 422 => Bad(DataNotFound)
              case 429 => Bad(RateLimit)
              case 500 => Bad(ServerError)
              case 503 => Bad(ServiceUnavailable)
              case code => warn(s"Unexpected response code $code; defaulting to ServerError(500)"); Bad(ServerError)
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


