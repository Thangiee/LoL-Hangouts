package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.{Match, TopChampion, ProfileSummary}

import scala.concurrent.Future

trait ViewProfileUseCase extends Interactor {

  def loadSummary(username: String, regionId: String): Future[ProfileSummary]

  def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]]

  def loadMatchHistories(username: String, regionId: String): Future[List[Match]]
}
