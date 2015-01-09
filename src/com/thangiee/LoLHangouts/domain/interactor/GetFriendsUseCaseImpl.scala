package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.domain.repository.FriendRepo
import com.thangiee.LoLHangouts.domain.utils._

case class GetFriendsUseCaseImpl(implicit friendRepo: FriendRepo) extends GetFriendsUseCase {

  override def loadFriendList(): List[Friend] = {
    friendRepo.getFriendList.fold(
      e => {
        error(s"[!] ${e.getMessage}", e.getCause)
        Nil
      },
      fl => {
        info("[+] friends loaded")
        fl
      }
    )
  }

  override def loadOnlineFriends(): List[Friend] = {
    info("[*] loading online friends")
    friendRepo.getOnlineFriend.fold(
      e => {
        error(s"[!] ${e.getMessage}", e.getCause); Nil
      },
      fl => {
        info("[+] online friends loaded")
        fl
      }
    )
  }
}
