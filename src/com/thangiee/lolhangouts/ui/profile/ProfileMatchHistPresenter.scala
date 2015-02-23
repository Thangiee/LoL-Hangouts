package com.thangiee.lolhangouts.ui.profile

import com.thangiee.lolhangouts.data.exception.DataAccessException
import com.thangiee.lolhangouts.data.exception.DataAccessException._
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCase
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileMatchHistPresenter(view: ProfileMatchHistView, viewProfileUseCase: ViewProfileUseCase) extends Presenter {

  def handleSetProfile(username: String, regionId: String): Unit = {
    view.showLoading()

    viewProfileUseCase.loadMatchHistory(username, regionId).map { hist =>
      if (hist.isEmpty) runOnUiThread {
        view.showDataNotFound()
      } else runOnUiThread {
        view.initializeViewData(hist)
        view.hideLoading()
      }
    } recover {
      case DataAccessException(_, DataNotFound) => runOnUiThread(view.showDataNotFound())
      case DataAccessException(_, GetDataError) => runOnUiThread(view.showGetDataError())
    }
  }
}
