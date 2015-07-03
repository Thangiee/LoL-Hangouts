package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources._
import com.thangiee.lolhangouts.data.datasources.entities.UserEntity
import com.thangiee.lolhangouts.data.usecases.entities.User

object UserMapper {

  def transform(userEntity: UserEntity): User = {
    User(
      userEntity.loginName,
      userEntity.inGameName,
      getFromId(userEntity.regionId),
      userEntity.statusMsg,
      userEntity.currentFriendNameChat,
      userEntity.groupNames.filter(_ != "**Default")
    )
  }
}
