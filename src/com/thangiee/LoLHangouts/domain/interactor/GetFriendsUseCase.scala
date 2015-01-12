package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend

import scala.concurrent.Future

trait GetFriendsUseCase extends Interactor {

  def loadOnlineFriends(): List[Friend]

  def loadFriendList(): List[Friend]

  def loadFriendByName(name: String): Future[Friend]
}
