package com.thangiee.LoLHangouts.login

import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.utils._

class LoginPresenter(view: LoginView, checkVerUseCase: CheckNewVerUseCase, loginUseCase: LoginUseCase) extends Presenter {

  override def resume(): Unit = {
    super.resume()
    loginUseCase.loadLoginInfo()
  }

  override def pause(): Unit = {
    loginUseCase.saveLoginInfo(view.getUsername, view.getPassword, view.isLoginOffline)
    super.pause()
  }

  def handleLogin(username: String, password: String): Unit = {
    view.showProgress()
    loginUseCase.login(username, password)
  }

  loginUseCase.onLogin(_ => {
    runOnUiThread(view.showLoginSuccess())
    Thread.sleep(700) // wait a bit for login success animation
    view.navigateToHome()
  })

  loginUseCase.onLoadLoginInfo((username, password, selectedRegion, isLoginOffline) => runOnUiThread {
    selectedRegion match {
      case Some(region) =>
        view.setTitle(region.name)
        view.setUsername(username)
        view.setPassword(password)
        view.showLoginOffline(isLoginOffline)
        if (!username.isEmpty) view.showSaveUsername(isEnable = true)
        if (!password.isEmpty) view.showSavePassword(isEnable = true)
        checkVerUseCase.checkForNewVersion()
      case None =>
        view.navigateBack()
    }
  })

  loginUseCase.onError(error =>
    runOnUiThread(view.showErrorMsg(error.message))
  )

  checkVerUseCase.onComplete((isNewVer, _) => runOnUiThread {
    if (isNewVer) view.showChangeLog()
  })
}
