package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.data.exception.DataAccessException
import com.thangiee.lolhangouts.data.exception.DataAccessException._
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileSummaryPresenter(view: ProfileSummaryView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadSummary(username, regionId).map { summary =>
      runOnUiThread {
        view.initializeViewData(summary)
        view.hideLoading()
      }
    } recover {
      case DataAccessException(_, DataNotFound) => runOnUiThread(view.showDataNotFound())
      case DataAccessException(_, GetDataError) => runOnUiThread(view.showGetDataError())
    }
  }
}
