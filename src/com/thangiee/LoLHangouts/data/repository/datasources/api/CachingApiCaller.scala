package com.thangiee.LoLHangouts.data.repository.datasources.api

import android.os.SystemClock
import com.thangiee.LoLHangouts.data.cache.MemCache
import com.thangiee.LoLHangouts.domain.utils.TagUtil
import thangiee.riotapi.core.RiotException._
import com.thangiee.LoLHangouts.domain.utils._
import thangiee.riotapi.core.{RiotApi, ApiCaller, ApiKey, RiotException}

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

class CachingApiCaller extends ApiCaller with TagUtil {
  RiotApi.key(Keys.productionKey)

  override def call(url: String)(implicit apiKey: ApiKey): Either[RiotException, String] = {
    for (attempt ← 1 to 5) {
      MemCache.get[String](url) match {
        case Some(cacheHit) =>
          return Right(cacheHit)
        case None           =>
          debug(s"[*] API caller attempt: $attempt - $url")
          Try(Http(url + apiKey.key).asString) match {
            case Success(response) =>
              response.code match {
                case 200 => MemCache.put(url, response.body); return Right(response.body) // cache and return the response
                case 400 => return Left(RiotException("Bad Request", BadRequest))
                case 401 => return Left(RiotException(s"Invalid API key: ${apiKey.key}", Unauthorized))
                case 404 => warn("[!] Requested data can not be found. Returning empty json"); return Right("{}")
                case 429 => return Left(RiotException("API key hit limit rate", RateLimit))
                case 500 => return Left(RiotException("Internal server error", ServerError))
                case 503 => info("[-] Service unavailable. ReAttempting...")
              }
            case Failure(e) => info("[-] Connection timeout. ReAttempting...")
          }
      }
      SystemClock.sleep(250) // wait a bit
    }

    warn("[-] Exhausted all api calling attempts")
    Left(RiotException("Service unavailable", ServiceUnavailable))
  }
}
