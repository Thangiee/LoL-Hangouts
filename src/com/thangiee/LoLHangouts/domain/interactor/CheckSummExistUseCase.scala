package com.thangiee.LoLHangouts.domain.interactor

import scala.concurrent.Future

trait CheckSummExistUseCase extends Interactor {

  def checkExists(summonerName: String, regionId: String): Future[Boolean]
}
