package com.thangiee.lolhangouts.domain.repository

import com.thangiee.lolhangouts.domain.entities.{Match, TopChampion, ProfileSummary}

trait ProfileDataRepo {

  def getSummary(name: String, regionId: String): Either[Exception, ProfileSummary]

  def getTopChampions(name: String, regionId: String): Either[Exception, List[TopChampion]]

  def getMatchHistory(name: String, regionId: String): Either[Exception, List[Match]]
}
