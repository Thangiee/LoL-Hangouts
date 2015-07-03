package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase.{GetProfileFailed, ProfileNotFound}
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileSummaryPresenter(view: ProfileSummaryView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadSummary(username, regionId).onSuccess {
      case Good(summary) => runOnUiThread {
        view.initializeViewData(summary)
        delay(100) { view.hideLoading() }
      }
      case Bad(ProfileNotFound) => runOnUiThread(view.showDataNotFound())
      case Bad(GetProfileFailed) => runOnUiThread(view.showGetDataError())
    }
  }
}
