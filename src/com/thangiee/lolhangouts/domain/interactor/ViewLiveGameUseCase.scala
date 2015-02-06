package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.LiveGame

import scala.concurrent.Future

trait ViewLiveGameUseCase extends Interactor {

  def loadLiveGame(username: String, regionId: String): Future[LiveGame]
}
