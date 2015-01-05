package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.domain.utils.Listener

trait GetFriendsUseCase extends Interactor {
  protected val loadFriendListListener = Listener[List[Friend]]()

  def onLoadFriendList(listener: List[Friend] => Unit) = loadFriendListListener.addListener(listener)

  def loadFriendList(): Unit
}
