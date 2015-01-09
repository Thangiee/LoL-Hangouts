package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.AppData
import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import com.thangiee.LoLHangouts.domain.utils._

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
