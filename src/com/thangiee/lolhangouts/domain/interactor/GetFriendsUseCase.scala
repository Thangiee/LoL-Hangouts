package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Friend

import scala.concurrent.Future

trait GetFriendsUseCase extends Interactor {

  def loadOnlineFriends(): List[Friend]

  def loadFriendList(): Future[List[Friend]]

  def loadFriendByName(name: String): Future[Friend]
}
