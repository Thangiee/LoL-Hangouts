package com.thangiee.LoLHangouts.ui.profile

import com.thangiee.LoLHangouts.domain.interactor.ViewProfileUseCase
import com.thangiee.LoLHangouts.ui.core.Presenter
import com.thangiee.LoLHangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class ProfileTopChampsPresenter(view: ProfileTopChampsView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadTopChamps(username, regionId) onComplete {
      case Success(champs) => runOnUiThread {
        if (champs.isEmpty) {
          view.showLoadingError("No Result", "Unable to find any champion data")
        } else {
          view.initializeViewData(champs)
          view.hideLoading()
        }
      }
      case Failure(e) => runOnUiThread {
        error(s"[!] ${e.getMessage}")
        view.showLoadingError("Opps", "Issues connecting to server.")
      }
    }
  }
}
