package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend

trait GetFriendsUseCase extends Interactor {

  def loadOnlineFriends(): List[Friend]

  def loadFriendList(): List[Friend]
}
