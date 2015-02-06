package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.AppData

import scala.concurrent.Future

trait GetAppDataUseCase extends Interactor {

  def loadAppData(): Future[AppData]
}
