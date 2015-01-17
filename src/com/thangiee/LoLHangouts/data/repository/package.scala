package com.thangiee.LoLHangouts.data

package object repository {
  implicit val appDataRepoImpl        = AppDataRepoImpl
  implicit val friendRepoImpl         = FriendRepoImpl
  implicit val messageRepoImpl        = MessageRepoImpl
  implicit val userRepoImpl           = UserRepoImpl
  implicit val profileSummaryRepoImpl = ProfileSummaryRepoImpl
}
