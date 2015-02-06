package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Friend
import com.thangiee.lolhangouts.domain.exception.UserInputException
import com.thangiee.lolhangouts.domain.repository.FriendRepo
import com.thangiee.lolhangouts.domain.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class GetFriendsUseCaseImpl(implicit friendRepo: FriendRepo) extends GetFriendsUseCase {

  override def loadFriendList(): Future[List[Friend]] = Future {
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
