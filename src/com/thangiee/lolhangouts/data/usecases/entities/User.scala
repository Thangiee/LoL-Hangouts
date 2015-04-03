package com.thangiee.lolhangouts.data.usecases.entities

case class User(
  loginName: String,
  inGameName: String,
  region: Region,
  statusMsg: String,
  currentFriendChat: Option[String] = None
  )
