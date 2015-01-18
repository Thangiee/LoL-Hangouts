package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.{TopChampion, ProfileSummary}
import com.thangiee.LoLHangouts.domain.repository.ProfileSummaryRepo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ViewProfileUseCaseImpl(implicit profileSummaryRepo: ProfileSummaryRepo) extends ViewProfileUseCase {

  override def loadSummary(username: String, regionId: String): Future[ProfileSummary] = Future {
    profileSummaryRepo.getProfileSummary(username.toLowerCase, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Profile summary loaded")
  }

  override def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]] = Future {
    profileSummaryRepo.getTopChampions(username.toLowerCase, regionId)
      .ifErrorThenLogAndThrow()
      .orElseLogAndReturn("Top Champions loaded")
  }
}
