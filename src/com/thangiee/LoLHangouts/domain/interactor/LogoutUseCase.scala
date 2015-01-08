package com.thangiee.LoLHangouts.domain.interactor

import scala.concurrent.Future


trait LogoutUseCase extends Interactor {

  def logout(): Future[Unit]
}
