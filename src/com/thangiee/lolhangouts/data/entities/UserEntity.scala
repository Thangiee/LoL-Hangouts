package com.thangiee.lolhangouts.data.entities

case class UserEntity(loginName: String, inGameName: String, regionId: String, statusMsg: String, currentFriendNameChat: Option[String] = None)
