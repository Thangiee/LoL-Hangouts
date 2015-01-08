package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.AppData

import scala.concurrent.Future

trait GetAppDataUseCase extends Interactor {

  def loadAppData(): Future[AppData]
}
