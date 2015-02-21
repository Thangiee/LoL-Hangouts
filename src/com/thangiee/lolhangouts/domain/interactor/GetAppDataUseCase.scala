package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.data.entities.mappers.AppDataMapper
import com.thangiee.lolhangouts.data.repository.datasources.AppDataFactory
import com.thangiee.lolhangouts.domain.entities.AppData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait GetAppDataUseCase extends Interactor {
  def loadAppData(): Future[AppData]
}

case class GetAppDataUseCaseImpl() extends GetAppDataUseCase {

  override def loadAppData(): Future[AppData] = Future {
    AppDataMapper.transform(AppDataFactory().createAppDataEntity())
  }
}