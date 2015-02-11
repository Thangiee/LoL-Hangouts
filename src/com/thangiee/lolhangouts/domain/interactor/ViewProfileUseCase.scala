package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.{Match, TopChampion, ProfileSummary}

import scala.concurrent.Future

trait ViewProfileUseCase extends Interactor {

  def loadSummary(username: String, regionId: String): Future[ProfileSummary]

  def loadTopChamps(username: String, regionId: String): Future[List[TopChampion]]

  def loadMatchHistory(username: String, regionId: String): Future[List[Match]]
}