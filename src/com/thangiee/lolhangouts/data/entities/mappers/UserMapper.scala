package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.UserEntity
import com.thangiee.lolhangouts.domain.entities.{Region, User}

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
