package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.AppDataFactory
import com.thangiee.lolhangouts.data.datasources.entities.mappers.AppDataMapper
import com.thangiee.lolhangouts.data.usecases.entities.AppData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GetAppDataUseCase extends Interactor {
  def loadAppData(): Future[AppData]
}

case class GetAppDataUseCaseImpl() extends GetAppDataUseCase {

  override def loadAppData(): Future[AppData] = Future {
    AppDataMapper.transform(AppDataFactory().createAppDataEntity())
  }
}