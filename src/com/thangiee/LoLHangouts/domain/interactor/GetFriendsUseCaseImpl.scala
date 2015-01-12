package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.domain.exception.UserInputException
import com.thangiee.LoLHangouts.domain.repository.FriendRepo
import com.thangiee.LoLHangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class GetFriendsUseCaseImpl(implicit friendRepo: FriendRepo) extends GetFriendsUseCase {

  override def loadFriendList(): List[Friend] = {
    friendRepo.getFriendList.fold(
      e => {
        error(s"[!] ${e.getMessage}", e.getCause)
        Nil
      },
      fl => {
        info("[+] friend list loaded")
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

  override def loadFriendByName(name: String): Future[Friend] = Future {
    if (name.isEmpty) throw UserInputException("friend name can not be empty")

    friendRepo.getFriendByName(name).fold(
      e => { error(s"[!] ${e.getMessage}", e.getCause); throw e },
      f => { info(s"[+] friend loaded by name: $name"); f}
    )
  }
}
