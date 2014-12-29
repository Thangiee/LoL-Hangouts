package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsNewVersion
import com.thangiee.LoLHangouts.domain.entities.Value.String.Version
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.utils.Listener


trait CheckNewVerUseCase extends Interactor {
  protected val completeListener = Listener[(IsNewVersion, Version)]()
  protected val errorListener = Listener[ErrorBundle]()

  def onComplete(listener: (IsNewVersion, Version) => Unit) = completeListener.addListener(listener.tupled)

  def onError(listener: ErrorBundle => Unit) = errorListener.addListener(listener)

  def checkForNewVersion(): Unit
}
