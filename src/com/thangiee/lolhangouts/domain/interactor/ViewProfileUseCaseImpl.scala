package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.{Match, TopChampion, ProfileSummary}
import com.thangiee.lolhangouts.domain.repository.ProfileDataRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ViewProfileUseCaseImpl(implicit profileDataRepo: ProfileDataRepo) extends ViewProfileUseCase {

  override def loadSummary(username: String, regionId: String): Future[ProfileSummary] = Future {
    profileDataRepo.getSummary(username.toLowerCase, regionId.toLowerCase)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Profile summary loaded")
  }

  override def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]] = Future {
    profileDataRepo.getTopChampions(username.toLowerCase, regionId.toLowerCase)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Top Champions loaded")
  }

  override def loadMatchHistory(username: String, regionId: String): Future[List[Match]] = Future {
    profileDataRepo.getMatchHistory(username.toLowerCase, regionId.toLowerCase)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Top Champions loaded")
  }
}