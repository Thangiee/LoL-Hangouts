package com.thangiee.LoLHangouts.domain.entities

case class User(loginName: String, inGameName: String, region: Region, statusMsg: String, currentFriendChat: Option[String] = None)
