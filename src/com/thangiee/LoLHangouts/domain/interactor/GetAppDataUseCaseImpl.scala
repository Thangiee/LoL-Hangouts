package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.AppDataRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class GetAppDataUseCaseImpl(implicit appDataRepo: AppDataRepo) extends GetAppDataUseCase {
  override def loadAppData(): Unit = Future {
    appDataRepo.getAppData.fold(
      error => throw error,
      data  => loadAppDataListener.notify(data)
    )
  }
}
