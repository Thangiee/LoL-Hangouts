package com.thangiee.LoLHangouts.domain.interactor

import com.thangiee.LoLHangouts.domain.entities.Value.Boolean.IsNewVersion
import com.thangiee.LoLHangouts.domain.entities.Value.String.Version

import scala.concurrent.Future


trait CheckNewVerUseCase extends Interactor {

  def checkForNewVersion(): Future[(IsNewVersion, Version)]
}
