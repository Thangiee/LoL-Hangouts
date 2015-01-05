package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.repository.FriendRepo

import scala.concurrent.Future

case class GetFriendsUseCaseImpl(implicit friendRepo: FriendRepo) extends GetFriendsUseCase {
  override def loadFriendList(): Unit = Future {
    friendRepo.getFriendList.fold(
      error => throw error,
      friends => loadFriendListListener.notify(friends)
    )
  }
}
