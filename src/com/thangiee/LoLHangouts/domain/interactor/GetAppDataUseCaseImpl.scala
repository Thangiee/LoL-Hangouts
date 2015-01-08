package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetAppDataUseCaseImpl(implicit appDataRepo: AppDataRepo) extends GetAppDataUseCase {

  override def loadAppData(): Unit = Future {
    appDataRepo.getAppData.left.map(e => { error(s"[!] ${e.getMessage}", e.getCause); throw e })
  }
}
