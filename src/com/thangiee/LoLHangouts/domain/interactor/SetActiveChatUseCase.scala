package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Friend

import scala.concurrent.Future

trait SetActiveChatUseCase extends Interactor {

  def setActiveChat(friend: Friend): Future[Unit]
}
