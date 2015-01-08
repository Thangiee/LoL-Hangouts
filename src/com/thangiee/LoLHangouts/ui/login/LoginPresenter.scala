package com.thangiee.LoLHangouts.ui.login

import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.exception.{AuthorizationException, ConnectionException, UserInputException}
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class LoginPresenter(view: LoginView, checkVerUseCase: CheckNewVerUseCase, loginUseCase: LoginUseCase) extends Presenter {

  override def resume(): Unit = {
    super.resume()
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

            checkVerUseCase.checkForNewVersion().onSuccess {
              case (isNewVersion, _) => if (isNewVersion) runOnUiThread(view.showChangeLog())
            }
          case None         =>
            view.navigateBack()
        }
      }
    }
  }

  override def pause(): Unit = {
    loginUseCase.saveLoginInfo(view.getUsername, view.getPassword, view.isLoginOffline)
    super.pause()
  }

  def handleLogin(username: String, password: String): Unit = {
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
}
