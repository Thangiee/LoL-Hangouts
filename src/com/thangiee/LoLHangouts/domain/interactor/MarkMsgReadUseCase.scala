package com.thangiee.LoLHangouts.domain.interactor

import scala.concurrent.Future

trait MarkMsgReadUseCase extends Interactor {
  
  def markAsRead(friendName: String): Future[Unit]

}
