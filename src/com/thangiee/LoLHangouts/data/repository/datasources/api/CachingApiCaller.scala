package com.thangiee.LoLHangouts.data.repository.datasources.api

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
    MemCache.get[String](url) match {
      case Some(cacheHit) =>
        Right(cacheHit)
      case None           =>
        Try(Http(url + apiKey.key).asString) match {
          case Success(response) =>
            response.code match {
              case 200 => MemCache.put(url, response.body); Right(response.body) // cache and return the response
              case 400 => Left(RiotException("Bad Request", BadRequest))
              case 401 => Left(RiotException(s"Invalid API key: ${apiKey.key}", Unauthorized))
              case 404 => Left(RiotException("Requested data can not be found", DataNotFound))
              case 429 => Left(RiotException("API key hit limit rate", RateLimit))
              case 500 => Left(RiotException("Internal server error", ServerError))
              case 503 => Left(RiotException("Service unavailable", ServiceUnavailable))
            }
          case Failure(e) => Left(RiotException("Service unavailable", ServiceUnavailable))
        }
    }
  }.left.map(e => {warn(s"[!] ${e.msg}"); e}) // log any RiotException
}
