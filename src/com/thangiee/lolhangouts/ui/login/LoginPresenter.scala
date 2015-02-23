package com.thangiee.lolhangouts.ui.login

import com.parse.{GetCallback, ParseException, ParseObject, ParseQuery}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.lolhangouts.data.exception.UseCaseException.{AuthenticationError, ConnectionError}
import com.thangiee.lolhangouts.data.exception.UserInputException._
import com.thangiee.lolhangouts.data.exception.{UseCaseException, UserInputException}
import com.thangiee.lolhangouts.data.usecases._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class LoginPresenter(view: LoginView, loginUseCase: LoginUseCase) extends Presenter {

  override def initialize(): Unit = {
    super.initialize()

    // don't run this code when first time opening the app since
    // the app will be redirected to region selection screen
    if (!Prefs.getBoolean("first_launch", true)) {
      info("[*] checking for new app version")
      loginUseCase.loadAppVersion().map { oldVer =>
        val currentVersion = view.getCurrentAppVersion
        if (oldVer != currentVersion) {
          // check if app was updated and if it is, show changelog
          loginUseCase.updateAppVersion(currentVersion)
          runOnUiThread(view.showChangeLog())
        } else {
          // check if new version is available in the Play store
          ParseQuery.getQuery("AppVersion").getFirstInBackground(new GetCallback[ParseObject] {
            override def done(storeVersion: ParseObject, e: ParseException): Unit = {
              if (e == null && currentVersion != storeVersion.getString("version")) {
                runOnUiThread(view.showUpdateApp(storeVersion.getString("version")))
              }
            }
          })
        }
      }
    }
  }

  override def resume(): Unit = {
    super.resume()
    info("[*] loading login info")
    loginUseCase.loadLoginInfo().onSuccess {
      case (username, password, selectedRegion, isLoginOffline) => selectedRegion match {
        case Some(region) =>
          view.setTitle(region.name)
          view.setUsername(username)
          view.setPassword(password)
          view.showLoginOffline(isLoginOffline)
          view.showSaveUsername(isEnable = !username.isEmpty)
          view.showSavePassword(isEnable = !password.isEmpty)
        case None         =>
          view.navigateBack()
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

    loginUseCase.login(username, password) map { _ =>
      runOnUiThread(view.showLoginSuccess())
      Thread.sleep(700) // wait a bit for login success animation
      view.navigateToHome()
    } recover {
      case UserInputException(msg, EmptyUsername)     => runOnUiThread(view.showBlankUsernameError())
      case UserInputException(msg, EmptyPassword)     => runOnUiThread(view.showBlankPasswordError())
      case UseCaseException(msg, ConnectionError)     => runOnUiThread(view.showConnectionError())
      case UseCaseException(msg, AuthenticationError) => runOnUiThread(view.showAuthenticationError())
    }
  }
}
