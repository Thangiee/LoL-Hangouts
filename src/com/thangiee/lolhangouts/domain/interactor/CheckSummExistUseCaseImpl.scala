package com.thangiee.lolhangouts.domain.interactor

import java.io.FileNotFoundException

import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

case class CheckSummExistUseCaseImpl() extends CheckSummExistUseCase {

  override def checkExists(summonerName: String, regionId: String): Future[Boolean] = Future {
    val url = s"https://acs.leagueoflegends.com/v1/players?name=${summonerName.replace(" ", "")}&region=$regionId"

    Try {
      io.Source.fromURL(url).mkString
    } match {
      case Success(_) => true
      case Failure(e) => e match {
        case e: FileNotFoundException => false
        case e: Exception             => error(s"[!] ${e.getMessage}", e.getCause); throw e
      }
    }
  }
}
