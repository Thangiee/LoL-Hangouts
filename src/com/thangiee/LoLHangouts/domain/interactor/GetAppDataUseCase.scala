package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.AppData
import com.thangiee.LoLHangouts.domain.utils.Listener

trait GetAppDataUseCase extends Interactor {
  protected val loadAppDataListener = Listener[AppData]()

  def onLoadAppData(listener: AppData => Unit) = loadAppDataListener.addListener(listener)

  def loadAppData(): Unit
}
