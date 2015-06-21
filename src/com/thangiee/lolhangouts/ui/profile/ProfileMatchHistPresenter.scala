package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileMatchHistPresenter(view: ProfileMatchHistView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadMatchHistory(username, regionId).onSuccess {
      case Good(hist) => runOnUiThread {
        if (hist.isEmpty) view.showDataNotFound()
        else {
          view.initializeViewData(hist)
          view.hideLoading()
        }
      }
      case Bad(ProfileNotFound) => runOnUiThread(view.showDataNotFound())
      case Bad(GetProfileFailed) => runOnUiThread(view.showGetDataError())
    }
  }
}
