package com.thangiee.lolhangouts.data.usecases

import java.io.FileNotFoundException

import com.thangiee.lolhangouts.data.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

trait CheckSummExistUseCase extends Interactor {
  def checkExists(summonerName: String, regionId: String): Future[Boolean]
}

case class CheckSummExistUseCaseImpl() extends CheckSummExistUseCase {

  override def checkExists(summonerName: String, regionId: String): Future[Boolean] = Future {
    val url = s"https://acs.leagueoflegends.com/v1/players?name=${summonerName.replace(" ", "")}&region=$regionId"

    Try {
      io.Source.fromURL(url).mkString
    } match {
      case Success(_) => true
      case Failure(e) => e match {
        case e: FileNotFoundException => false
        case _ => e.printStackTrace(); false
      }
    }
  }
}