package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.{TopChampion, ProfileSummary}

import scala.concurrent.Future

trait ViewProfileUseCase extends Interactor {

  def loadSummary(username: String, regionId: String): Future[ProfileSummary]

  def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]]
}
