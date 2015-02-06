package com.thangiee.lolhangouts.domain.interactor

import scala.concurrent.Future

trait DeleteMsgUseCase extends Interactor {

  def deleteAllMessages(friendName: String): Future[Unit]
}
