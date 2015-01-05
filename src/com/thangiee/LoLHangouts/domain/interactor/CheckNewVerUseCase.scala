package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsNewVersion
import com.thangiee.LoLHangouts.domain.entities.Value.String.Version
import com.thangiee.LoLHangouts.domain.exception.ErrorBundle
import com.thangiee.LoLHangouts.domain.utils.Listener


trait CheckNewVerUseCase extends Interactor {
  protected val checkForNewVersionListener = Listener[(IsNewVersion, Version)]()
  protected val errorListener = Listener[ErrorBundle]()

  def onCheckForNewVersion(listener: (IsNewVersion, Version) => Unit) = checkForNewVersionListener.addListener(listener.tupled)

  def checkForNewVersion(): Unit
}
