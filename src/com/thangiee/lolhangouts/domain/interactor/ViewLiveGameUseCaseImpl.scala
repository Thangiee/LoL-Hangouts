package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.LiveGame
import com.thangiee.lolhangouts.domain.repository.LiveGameRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ViewLiveGameUseCaseImpl(implicit liveGameRepo: LiveGameRepo) extends ViewLiveGameUseCase {

  override def loadLiveGame(username: String, regionId: String): Future[LiveGame] = Future {
    liveGameRepo.getGame(username, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Live game loaded")
  }
}
