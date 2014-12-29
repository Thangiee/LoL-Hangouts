package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.utils.Listener


trait SimpleInteractor[T] extends Interactor {
  protected val completeListener = Listener[T]()
  protected val errorListener = Listener[ErrorBundle]()

  def onComplete(listener: T => Unit) = completeListener.addListener(listener)

  def onError(listener: ErrorBundle => Unit) = errorListener.addListener(listener)
}
