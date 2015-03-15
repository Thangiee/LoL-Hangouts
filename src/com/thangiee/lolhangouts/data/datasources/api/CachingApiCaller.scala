package com.thangiee.lolhangouts.data.datasources.api

import java.net.SocketTimeoutException

import android.os.SystemClock
import com.thangiee.lolhangouts.data.datasources.cache.MemCache
import com.thangiee.lolhangouts.data.utils.{TagUtil, _}
import thangiee.riotapi.core.RiotException._
import thangiee.riotapi.core.{ApiCaller, RiotApi, RiotException}

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

trait CachingApiCaller extends ApiCaller with TagUtil {

  override def call(url: String): Try[String] = {
    for (attempt ← 1 to 7) {
      MemCache.get[String](url) match {
        case Some(cacheHit) =>
          return Success(cacheHit)
        case None           => // cache missed, call the API
          RiotApi.key = if (attempt == 7) Keys.productionKey else Keys.testKey
          debug(s"[*] API caller attempt: $attempt - $url${RiotApi.key}") // make use of test keys until last attempt
          Try(Http(url + RiotApi.key).asString) match {
            case Success(response) =>
              response.code match {
                case 200 => MemCache.put(url, response.body); return Success(response.body) // cache and return the response
                case 400 => return Failure(RiotException("400: Bad request", BadRequest))
                case 401 => warn(s"[!] Invalid API key: ${RiotApi.key}")
                case 404 => warn("[!] Requested data can not be found. Returning empty json"); return Success("{}")
                case 429 => warn("[-] API key hit limit rate: " + RiotApi.key)
                case 500 => return Failure(RiotException("500: Internal server error", ServerError))
                case 503 => info("[-] Service unavailable. ReAttempting...")
              }
            case Failure(e) => e match {
              case e: SocketTimeoutException => info("[-] Connection timeout. ReAttempting...")
              case _                         => throw e
            }
          }
      }
      SystemClock.sleep(250) // wait a bit
    }

    warn("[-] Exhausted all api calling attempts")
    Failure(RiotException("503: Service unavailable", ServiceUnavailable))
  }
}

object CachingApiCaller extends CachingApiCaller