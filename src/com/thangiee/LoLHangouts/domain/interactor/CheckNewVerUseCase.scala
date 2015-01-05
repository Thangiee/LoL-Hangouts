package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsNewVersion
import com.thangiee.LoLHangouts.domain.entities.Value.String.Version
import com.thangiee.LoLHangouts.domain.utils.Listener


trait CheckNewVerUseCase extends Interactor {
  protected val checkForNewVersionListener = Listener[(IsNewVersion, Version)]()

  def onCheckForNewVersion(listener: (IsNewVersion, Version) => Unit) = checkForNewVersionListener.addListener(listener.tupled)

  def checkForNewVersion(): Unit
}
