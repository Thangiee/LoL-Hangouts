package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.domain.interactor.ViewProfileUseCase
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class ProfileSummaryPresenter(view: ProfileSummaryView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadSummary(username, regionId) onComplete {
      case Success(summary) => runOnUiThread {
        view.initializeViewData(summary)
        view.hideLoading()
      }
      case Failure(e) => runOnUiThread {
        error(s"[!] ${e.getMessage}")
        view.showLoadingError("Opps", e.getMessage)
      }
    }
  }
}
