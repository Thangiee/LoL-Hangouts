package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Message

import scala.concurrent.Future

trait SendMsgUseCase extends Interactor {

  def sendMessage(message: Message): Future[Unit]
}
