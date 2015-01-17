package com.thangiee.LoLHangouts.domain.repository

import com.thangiee.LoLHangouts.domain.entities.ProfileSummary

trait ProfileSummaryRepo {

  def getProfileSummary(name: String, regionId: String): Either[Exception, ProfileSummary]
}
