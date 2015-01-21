package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.LiveGame

import scala.concurrent.Future

trait ViewLiveGameUseCase extends Interactor {

  def loadLiveGame(username: String, regionId: String): Future[LiveGame]
}
