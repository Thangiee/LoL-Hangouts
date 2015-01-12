package com.thangiee.LoLHangouts.ui.login

import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.exception.{AuthorizationException, ConnectionException, UserInputException}
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class LoginPresenter(view: LoginView, loginUseCase: LoginUseCase) extends Presenter {

  override def resume(): Unit = {
    super.resume()
    info("[*] loading login info")
    loginUseCase.loadLoginInfo().onSuccess {
      case (username, password, selectedRegion, isLoginOffline) => runOnUiThread {
        selectedRegion match {
          case Some(region) =>
            view.setTitle(region.name)
            view.setUsername(username)
            view.setPassword(password)
            view.showLoginOffline(isLoginOffline)

            if (!username.isEmpty) view.showSaveUsername(isEnable = true)
            if (!password.isEmpty) view.showSavePassword(isEnable = true)

            checkAndUpdateAppVersion()
          case None         =>
            view.navigateBack()
        }
      }
    }
  }

  override def pause(): Unit = {
    info("[*] saving login info")
    loginUseCase.saveLoginInfo(view.getUsername, view.getPassword, view.isLoginOffline)
    super.pause()
  }

  def handleLogin(username: String, password: String): Unit = {
    info("[*] attempting to login")
    view.showProgress()
    loginUseCase.login(username, password) onComplete {
      case Success(_) => onLoginSuccess()
      case Failure(e) => showFailure(e)
    }
  }

  def onLoginSuccess(): Unit = {
    runOnUiThread(view.showLoginSuccess())
    Thread.sleep(700) // wait a bit for login success animation
    view.navigateToHome()
  }

  def showFailure(e: Throwable): Unit = e match {
    case e@(_: UserInputException | _: AuthorizationException | _: ConnectionException) =>
      runOnUiThread(view.showErrorMsg(e.getMessage))
  }

  private def checkAndUpdateAppVersion(): Unit = {
    info("[*] checking for new app version")
    loginUseCase.loadAppVersion().map { oldVer =>
      val currentVersion = view.getCurrentAppVersion
      if (oldVer != currentVersion) {
        loginUseCase.updateAppVersion(currentVersion)
        runOnUiThread(view.showChangeLog())
      }
    }
  }
}
