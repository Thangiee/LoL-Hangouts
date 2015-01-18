package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.{TopChampion, ProfileSummary}

trait ProfileSummaryRepo {

  def getProfileSummary(name: String, regionId: String): Either[Exception, ProfileSummary]

  def getTopChampions(name: String, regionId: String): Either[Exception, List[TopChampion]]
}
