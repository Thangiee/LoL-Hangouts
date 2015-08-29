package com.thangiee.lolhangouts.ui.login

import com.parse.{GetCallback, ParseException, ParseObject, ParseQuery}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.usecases.LoginUseCase._
import com.thangiee.lolhangouts.data.usecases._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils._

import scala.concurrent.ExecutionContext.Implicits.global

class LoginPresenter(view: LoginView, loginUseCase: LoginUseCase) extends Presenter {
  private var isGuestMode = false

  override def initialize(): Unit = {
    super.initialize()

    // don't run this code when first time opening the app since
    // the app will be redirected to region selection screen
    if (!Cached.isFirstLaunch) {
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
          view.navigateBack() // redirected to region selection screen
      }
    }
  }

  override def pause(): Unit = {
    info("[*] saving login info")
    loginUseCase.saveLoginInfo(view.getUsername, view.getPassword, view.isLoginOffline, isGuestMode)
    super.pause()
  }

  def handleLogin(username: String, password: String): Unit = {
    info("[*] attempting to login")
    isGuestMode = false
    view.setLoginState(LoginView.LoadingState)

    loginUseCase.login(username, password, view.isLoginOffline).onSuccess {
      case Good(_)                  =>
        runOnUiThread(view.setLoginState(LoginView.SuccessState))
        Thread.sleep(700) // wait a bit for login success animation
        view.navigateToHome(isGuestMode = false)
      case Bad(EmptyUsername)       => runOnUiThread(view.showBlankUsernameError())
      case Bad(EmptyPassword)       => runOnUiThread(view.showBlankPasswordError())
      case Bad(EmptyUserAndPass)    => runOnUiThread {view.showBlankUsernameError(); view.showBlankPasswordError()}
      case Bad(ConnectionError)     => runOnUiThread(view.showConnectionError())
      case Bad(AuthenticationError) => runOnUiThread(view.showAuthenticationError())
      case Bad(InternalError)       => runOnUiThread(view.showConnectionError()) //todo:
    }
  }

  def handleGuestLogin(): Unit = {
    isGuestMode = true
    view.setGuessLoginState(LoginView.LoadingState)

    delay(mills = 350) {
      view.setGuessLoginState(LoginView.SuccessState)
      delay(mills = 500) {
        view.navigateToHome(isGuestMode = true)
      }
    }
  }
}
