package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Friend

import scala.concurrent.Future

trait SetActiveChatUseCase extends Interactor {

  def setActiveChat(friend: Friend): Future[Unit]
}
