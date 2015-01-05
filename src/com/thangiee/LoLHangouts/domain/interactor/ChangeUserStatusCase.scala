package com.thangiee.LoLHangouts.domain.interactor

trait ChangeUserStatusCase extends Interactor {

  def appearOnline(): Unit

  def appearAway(): Unit

  def appearOffline(): Unit

  def setStatusMsg(msg: String): Unit
}
