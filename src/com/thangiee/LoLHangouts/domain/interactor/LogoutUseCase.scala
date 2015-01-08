package com.thangiee.LoLHangouts.domain.interactor

import java.util.concurrent.Future

trait LogoutUseCase extends Interactor {

  def logout(): Future[Unit]
}
