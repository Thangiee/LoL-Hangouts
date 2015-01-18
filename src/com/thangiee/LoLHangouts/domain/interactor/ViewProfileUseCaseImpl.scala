package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.{Match, TopChampion, ProfileSummary}
import com.thangiee.LoLHangouts.domain.repository.ProfileDataRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ViewProfileUseCaseImpl(implicit profileDataRepo: ProfileDataRepo) extends ViewProfileUseCase {

  override def loadSummary(username: String, regionId: String): Future[ProfileSummary] = Future {
    profileDataRepo.getSummary(username.toLowerCase, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Profile summary loaded")
  }

  override def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]] = Future {
    profileDataRepo.getTopChampions(username.toLowerCase, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Top Champions loaded")
  }

  override def loadMatchHistories(username: String, regionId: String): Future[List[Match]] = ???
}
