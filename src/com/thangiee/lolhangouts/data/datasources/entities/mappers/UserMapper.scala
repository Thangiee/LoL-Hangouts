package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.UserEntity
import com.thangiee.lolhangouts.data.usecases.entities.{Region, User}

object UserMapper {

  def transform(userEntity: UserEntity): User = {
    User(
      userEntity.loginName,
      userEntity.inGameName,
      Region.getFromId(userEntity.regionId),
      userEntity.statusMsg,
      userEntity.currentFriendNameChat
    )
  }
}
