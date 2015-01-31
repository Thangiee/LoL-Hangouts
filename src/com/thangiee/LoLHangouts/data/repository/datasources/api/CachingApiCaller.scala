package com.thangiee.LoLHangouts.data.repository.datasources.api

import java.net.SocketTimeoutException

import android.os.SystemClock
import com.thangiee.LoLHangouts.data.cache.MemCache
import com.thangiee.LoLHangouts.domain.utils.TagUtil
import thangiee.riotapi.core.RiotException._
import com.thangiee.LoLHangouts.domain.utils._
import thangiee.riotapi.core.{RiotApi, ApiCaller, ApiKey, RiotException}

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

class CachingApiCaller extends ApiCaller with TagUtil {

  override def call(url: String)(implicit apiKey: ApiKey): Either[RiotException, String] = {
    for (attempt ← 1 to 7) {
      MemCache.get[String](url) match {
        case Some(cacheHit) =>
          return Right(cacheHit)
        case None           => // cache missed, call the API
          RiotApi.key(if (attempt == 10) Keys.productionKey else Keys.testKey)
          debug(s"[*] API caller attempt: $attempt - $url - ${apiKey.key}") // make use of test keys until last attempt
          Try(Http(url + apiKey.key).asString) match {
            case Success(response) =>
              response.code match {
                case 200 => MemCache.put(url, response.body); return Right(response.body) // cache and return the response
                case 400 => return Left(RiotException("Bad Request", BadRequest))
                case 401 => warn(s"Invalid API key: ${apiKey.key}")
                case 404 => warn("[!] Requested data can not be found. Returning empty json"); return Right("{}")
                case 429 => warn("[-] API key hit limit rate: " + apiKey.key)
                case 500 => return Left(RiotException("Internal server error", ServerError))
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
    Left(RiotException("Service unavailable", ServiceUnavailable))
  }
}
