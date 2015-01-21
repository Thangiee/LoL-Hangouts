package com.thangiee.LoLHangouts.domain.interactor

import scala.concurrent.Future

trait AddFriendUseCase extends Interactor {

  def addFriend(name: String): Future[Unit]
}
