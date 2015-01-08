package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Value.Boolean._
import com.thangiee.LoLHangouts.domain.entities.Value.String._
import com.thangiee.LoLHangouts.domain.repository.AppDataRepo
import com.thangiee.LoLHangouts.domain.utils.Logger._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CheckNewVerUseCaseImpl(implicit appDataRepo: AppDataRepo) extends CheckNewVerUseCase {

  override def checkForNewVersion(): Future[(IsNewVersion, Version)] = Future {
    appDataRepo.getAppData.fold(
      e    => {
        error(s"[!] ${e.getMessage}", e.getCause)
        throw e
      },
      data => {
        info("[+] finish check for new version")
        if (data.isNewVersion) appDataRepo.updateAppVersion()
        (data.isNewVersion, data.version)
      }
    )
  }
}
