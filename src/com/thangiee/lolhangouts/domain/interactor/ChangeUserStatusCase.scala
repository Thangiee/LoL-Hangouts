package com.thangiee.lolhangouts.domain.interactor

import scala.concurrent.Future

trait ChangeUserStatusCase extends Interactor {

  def appearOnline(): Future[Unit]

  def appearAway(): Future[Unit]

  def appearOffline(): Future[Unit]

  def setStatusMsg(msg: String): Future[Unit]
}
