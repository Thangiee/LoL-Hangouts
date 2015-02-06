package com.thangiee.lolhangouts.data

package object repository {
  implicit val appDataRepoImpl     = AppDataRepoImpl
  implicit val friendRepoImpl      = FriendRepoImpl
  implicit val messageRepoImpl     = MessageRepoImpl
  implicit val userRepoImpl        = UserRepoImpl
  implicit val profileDataRepoImpl = ProfileDataRepoImpl
  implicit val liveGameRepoImpl    = LiveGameRepoImpl
}
