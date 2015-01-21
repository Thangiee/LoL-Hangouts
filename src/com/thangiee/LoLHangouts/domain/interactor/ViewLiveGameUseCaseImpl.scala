package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.LiveGame
import com.thangiee.LoLHangouts.domain.repository.LiveGameRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ViewLiveGameUseCaseImpl(implicit liveGameRepo: LiveGameRepo) extends ViewLiveGameUseCase {

  override def loadLiveGame(username: String, regionId: String): Future[LiveGame] = Future {
    liveGameRepo.getGame(username, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Live game loaded")
  }
}
