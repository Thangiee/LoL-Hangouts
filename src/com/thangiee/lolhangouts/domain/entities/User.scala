package com.thangiee.lolhangouts.domain.entities

case class User(loginName: String, inGameName: String, region: Region, statusMsg: String, currentFriendChat: Option[String] = None)
