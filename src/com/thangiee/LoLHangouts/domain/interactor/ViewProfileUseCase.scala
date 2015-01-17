package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.ProfileSummary

import scala.concurrent.Future

trait ViewProfileUseCase extends Interactor {

  def loadSummary(username: String, regionId: String): Future[ProfileSummary]
}
