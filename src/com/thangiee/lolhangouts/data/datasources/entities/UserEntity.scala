package com.thangiee.lolhangouts.data.datasources.entities

case class UserEntity(
  loginName: String,
  inGameName: String,
  regionId: String,
  statusMsg: String,
  currentFriendNameChat: Option[String] = None,
  groupNames: Seq[String]
  )
