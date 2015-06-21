package com.thangiee.lolhangouts.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.balysv.materialmenu.MaterialMenuDrawable
import com.dd.CircularProgressButton
import com.rengwuxian.materialedittext.MaterialEditText
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.LoginUseCaseImpl
import com.thangiee.lolhangouts.ui.core.TActivity
import com.thangiee.lolhangouts.ui.main.MainActivity
import com.thangiee.lolhangouts.ui.regionselection.RegionSelectionActivity
import com.thangiee.lolhangouts.ui.utils._

import scala.util.Try

class LoginActivity extends TActivity with LoginView {
  private lazy val userEditText       = find[MaterialEditText](R.id.et_username)
  private lazy val passwordEditText   = find[MaterialEditText](R.id.et_password)
  private lazy val logInBtn           = find[CircularProgressButton](R.id.btn_login)
  private lazy val guestLogInBtn      = find[CircularProgressButton](R.id.btn_guest)
  private lazy val saveUserSwitch     = find[SwitchCompat](R.id.cb_save_user)
  private lazy val savePassSwitch     = find[SwitchCompat](R.id.cb_save_pass)
  private lazy val offlineLoginSwitch = find[SwitchCompat](R.id.cb_offline_login)

  override protected val presenter = new LoginPresenter(this, LoginUseCaseImpl())
  override val layoutId  = R.layout.act_login_screen

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    overridePendingTransition(R.anim.right_slide_in, R.anim.stay_still)

    logInBtn.setIndeterminateProgressMode(true)
    logInBtn.onClick { v: View =>
      if (logInBtn.getProgress == LoginView.ErrorState) logInBtn.setProgress(LoginView.NormalState)
      else presenter.handleLogin(userEditText.txt2str, passwordEditText.txt2str)
    }

    guestLogInBtn.setIndeterminateProgressMode(true)
    guestLogInBtn.onClick((v: View) => presenter.handleGuestLogin())

    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(navigateBack())
  }

  override def onStart(): Unit = {
    super.onStart()
    presenter.initialize()
  }

  override def onResume(): Unit = {
    super.onResume()
    presenter.resume()
  }

  override def onPause(): Unit = {
    super.onPause()
    presenter.pause()
  }

  override def onStop(): Unit = {
    presenter.shutdown()
    super.onStop()
  }

  override def setLoginState(state: Int): Unit = logInBtn.setProgress(state)

  override def setGuessLoginState(state: Int): Unit = guestLogInBtn.setProgress(state)

  override def showChangeLog(): Unit = super.showChangeLog()

  override def navigateToHome(isGuestMode: Boolean): Unit = {
    finish()
    ctx.startActivity(MainActivity(isGuestMode))
  }

  override def navigateBack(): Unit = {
    finish()
    startActivity[RegionSelectionActivity]
  }

  override def setTitle(title: String): Unit = getSupportActionBar.setTitle(title)

  override def setPassword(password: String): Unit = runOnUiThread(passwordEditText.setText(password))

  override def setUsername(name: String): Unit = runOnUiThread(userEditText.setText(name))

  override def getPassword: String = if (savePassSwitch.isChecked) passwordEditText.txt2str else ""

  override def getUsername: String = if (saveUserSwitch.isChecked) userEditText.txt2str else ""

  override def isLoginOffline: Boolean = offlineLoginSwitch.isChecked

  override def showLoginOffline(isEnable: Boolean): Unit = offlineLoginSwitch.setChecked(isEnable)

  override def showSaveUsername(isEnable: Boolean): Unit = saveUserSwitch.setChecked(isEnable)

  override def showSavePassword(isEnable: Boolean): Unit = savePassSwitch.setChecked(isEnable)

  override def getCurrentAppVersion: String = getPackageManager.getPackageInfo(ctx.getPackageName, 0).versionName

  override def showUpdateApp(version: String): Unit = new Builder(ctx)
    .title("Update Available!")
    .content(s"New app version $version is now available in the Play store.")
    .negativeText("Later")
    .positiveText("Update")
    .onPositive((dialog) => Try(startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName))))) // open app page in play store
    .show()

  override def showBlankUsernameError(): Unit = {
    userEditText.shake()
    R.string.err_empty_user.croutonWarn()
    logInBtn.setProgress(LoginView.ErrorState)
  }

  override def showBlankPasswordError(): Unit = {
    passwordEditText.shake()
    R.string.err_empty_pass.croutonWarn()
    logInBtn.setProgress(LoginView.ErrorState)
  }

  override def showAuthenticationError(): Unit = {
    R.string.err_authentication.croutonWarn()
    logInBtn.setProgress(LoginView.ErrorState)
  }

  override def showConnectionError(): Unit = {
    R.string.err_connect_to_server.croutonWarn()
    logInBtn.setProgress(LoginView.ErrorState)
  }

}