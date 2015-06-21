package com.thangiee.lolhangouts.data.usecases.entities

import com.thangiee.lolchat.region.Region

case class User(
  loginName: String,
  inGameName: String,
  region: Region,
  statusMsg: String,
  currentFriendChat: Option[String] = None
  )
