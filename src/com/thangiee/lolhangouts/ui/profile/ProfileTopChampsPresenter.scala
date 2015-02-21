package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.domain.exception.DataAccessException
import com.thangiee.lolhangouts.domain.exception.DataAccessException._
import com.thangiee.lolhangouts.domain.interactor.ViewProfileUseCase
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileTopChampsPresenter(view: ProfileTopChampsView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadTopChamps(username, regionId).map { champs =>
      if (champs.isEmpty) runOnUiThread {
        view.showDataNotFound()
      } else runOnUiThread {
        view.initializeViewData(champs)
        view.hideLoading()
      }
    } recover {
      case DataAccessException(_, DataNotFound) => runOnUiThread(view.showDataNotFound())
      case DataAccessException(_, GetDataError) => runOnUiThread(view.showGetDataError())
    }
  }
}
