package com.thangiee.lolhangouts.domain.interactor

import scala.concurrent.Future

trait CheckSummExistUseCase extends Interactor {

  def checkExists(summonerName: String, regionId: String): Future[Boolean]
}
