package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.AppData
import com.thangiee.lolhangouts.domain.repository.AppDataRepo
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetAppDataUseCaseImpl(implicit appDataRepo: AppDataRepo) extends GetAppDataUseCase {

  override def loadAppData(): Future[AppData] = Future {
    appDataRepo.getAppData.fold(e => {
      error(s"[!] ${e.getMessage}", e.getCause)
      throw e
    },
      data => {
        info("[+] app data loaded")
        data
      }
    )
  }
}
