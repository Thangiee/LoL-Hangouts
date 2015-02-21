package com.thangiee.lolhangouts.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.balysv.materialmenu.MaterialMenuDrawable
import com.dd.CircularProgressButton
import com.rengwuxian.materialedittext.MaterialEditText
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.domain.interactor.LoginUseCaseImpl
import com.thangiee.lolhangouts.ui.core.TActivity
import com.thangiee.lolhangouts.ui.main.MainActivity
import com.thangiee.lolhangouts.ui.regionselection.RegionSelectionActivity
import com.thangiee.lolhangouts.utils._
import org.jivesoftware.smack.SmackAndroid

import scala.util.Try

class LoginActivity extends TActivity with LoginView {
  lazy val userEditText       = find[MaterialEditText](R.id.et_username)
  lazy val passwordEditText   = find[MaterialEditText](R.id.et_password)
  lazy val logInButton        = find[CircularProgressButton](R.id.btn_login)
  lazy val saveUserSwitch     = find[SwitchCompat](R.id.cb_save_user)
  lazy val savePassSwitch     = find[SwitchCompat](R.id.cb_save_pass)
  lazy val offlineLoginSwitch = find[SwitchCompat](R.id.cb_offline_login)

  override val presenter = new LoginPresenter(this, LoginUseCaseImpl())
  override val layoutId  = R.layout.act_login_screen

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    SmackAndroid.init(ctx)
    overridePendingTransition(R.anim.right_slide_in, R.anim.stay_still)

    logInButton.setIndeterminateProgressMode(true)
    logInButton.onClick { v: View =>
      if (logInButton.getProgress == -1) logInButton.setProgress(0)
      else presenter.handleLogin(userEditText.txt2str, passwordEditText.txt2str)
    }

    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(navigateBack())
    getSupportActionBar.setTitle(null)
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

  override def showProgress(): Unit = logInButton.setProgress(50)

  override def hideProgress(): Unit = logInButton.setProgress(0)

  override def showLoginSuccess(): Unit = logInButton.setProgress(100)

  override def showChangeLog(): Unit = super.showChangeLog()

  override def navigateToHome(): Unit = {
    finish()
    startActivity[MainActivity]
  }

  override def navigateBack(): Unit = {
    finish()
    startActivity[RegionSelectionActivity]
  }

  override def setTitle(title: String): Unit = find[TextView](R.id.tv_region_name).setText(title)

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
    logInButton.setProgress(-1)
  }

  override def showBlankPasswordError(): Unit = {
    passwordEditText.shake()
    R.string.err_empty_pass.croutonWarn()
    logInButton.setProgress(-1)
  }

  override def showAuthenticationError(): Unit = {
    R.string.err_authentication.croutonWarn()
    logInButton.setProgress(-1)
  }

  override def showConnectionError(): Unit = {
    R.string.err_connect_to_server.croutonWarn()
    logInButton.setProgress(-1)
  }
}