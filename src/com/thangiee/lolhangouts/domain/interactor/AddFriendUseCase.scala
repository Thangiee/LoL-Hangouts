package com.thangiee.lolhangouts.domain.interactor

import scala.concurrent.Future

trait AddFriendUseCase extends Interactor {

  def addFriend(name: String): Future[Unit]
}
