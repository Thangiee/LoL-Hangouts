package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.UserEntity
import com.thangiee.LoLHangouts.domain.entities.{Region, User}

case class UserMapper() {

  def transform(userEntity: UserEntity): User = {
    User(
      userEntity.loginName,
      userEntity.inGameName,
      Region.getFromId(userEntity.regionId),
      userEntity.currentFriendNameChat
    )
  }
}
