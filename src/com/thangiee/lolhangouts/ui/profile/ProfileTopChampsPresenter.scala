package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase.{ProfileNotFound, GetProfileFailed}
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileTopChampsPresenter(view: ProfileTopChampsView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadTopChamps(username, regionId).onSuccess {
      case Good(champs)       => runOnUiThread {
        if (champs.isEmpty) view.showDataNotFound()
        else runOnUiThread {
          view.initializeViewData(champs)
          view.hideLoading()
        }
      }
      case Bad(ProfileNotFound)  => runOnUiThread(view.showDataNotFound())
      case Bad(GetProfileFailed) => runOnUiThread(view.showGetDataError())
    }
  }
}
