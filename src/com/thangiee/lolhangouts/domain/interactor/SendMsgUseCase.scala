package com.thangiee.lolhangouts.domain.interactor

import com.thangiee.lolhangouts.domain.entities.Message

import scala.concurrent.Future

trait SendMsgUseCase extends Interactor {

  def sendMessage(message: Message): Future[Unit]
}
