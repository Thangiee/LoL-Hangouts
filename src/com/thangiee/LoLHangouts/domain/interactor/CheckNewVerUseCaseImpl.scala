package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.AppDataRepo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CheckNewVerUseCaseImpl(implicit appDataRepo: AppDataRepo) extends CheckNewVerUseCase {

  override def checkForNewVersion(): Unit = Future {
    appDataRepo.getAppData.fold(
      error =>
        errorListener.notify(error),
      data  => {
        completeListener.notify((data.isNewVersion, data.version))
        if (data.isNewVersion) appDataRepo.updateAppVersion()
      }
    )
  }
}
