package com.thangiee.LoLHangouts.ui.login

import android.os.Bundle
import android.view.View
import android.widget.{CheckBox, EditText, ImageView}
import com.balysv.materialmenu.MaterialMenuDrawable
import com.dd.CircularProgressButton
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{MainActivity, RegionSelectionActivity, TActivity}
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.interactor.LoginUseCaseImpl
import com.thangiee.LoLHangouts.utils._

class LoginActivity extends TActivity with LoginView {
  lazy val userEditText         = find[EditText](R.id.et_username)
  lazy val passwordEditText     = find[EditText](R.id.et_password)
  lazy val logInButton          = find[CircularProgressButton](R.id.btn_login)
  lazy val saveUserCheckBox     = find[CheckBox](R.id.cb_save_user)
  lazy val savePassCheckBox     = find[CheckBox](R.id.cb_save_pass)
  lazy val offlineLoginCheckBox = find[CheckBox](R.id.cb_offline_login)

  override val presenter    = new LoginPresenter(this, LoginUseCaseImpl())
  override val layoutId     = R.layout.login

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)

    logInButton.setIndeterminateProgressMode(true)
    logInButton.onClick { v: View =>
      if (logInButton.getProgress == -1) logInButton.setProgress(0)
      else presenter.handleLogin(userEditText.getText.toString, passwordEditText.getText.toString)
    }

    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(navigateBack())
  }

  override def onStart(): Unit = {
    super.onStart()
    presenter.initialize()
    find[ImageView](R.id.img_login_bg).setImageResource(R.drawable.shadow_isles_640)
  }

  override def onResume(): Unit = {
    super.onResume()
    presenter.resume()

    // check the checkbox if those fields are not empty
    if (userEditText.length() != 0) saveUserCheckBox.setChecked(true)
    if (passwordEditText.length() != 0) savePassCheckBox.setChecked(true)
  }

  override def onPause(): Unit = {
    super.onPause()
    presenter.pause()
  }

  override def onStop(): Unit = {
    find[ImageView](R.id.img_login_bg).setImageDrawable(null)
    presenter.shutdown()
    super.onStop()
  }

  override def showProgress(): Unit = logInButton.setProgress(50)

  override def hideProgress(): Unit = logInButton.setProgress(0)

  override def showLoginSuccess(): Unit = logInButton.setProgress(100)

  override def showErrorMsg(msg: String): Unit = runOnUiThread {
    msg.croutonWarn()
    logInButton.setProgress(-1)
  }

  override def showChangeLog(): Unit = super.showChangeLog()

  override def navigateToHome(): Unit = {
    finish()
    startActivity[MainActivity]
  }

  override def navigateBack(): Unit = {
    finish()
    startActivity[RegionSelectionActivity]
  }

  override def setTitle(title: String): Unit = toolbar.setTitle(title)

  override def setPassword(password: String): Unit = passwordEditText.setText(password)

  override def setUsername(name: String): Unit = userEditText.setText(name)

  override def getPassword: String = if (savePassCheckBox.isChecked) passwordEditText.getText.toString else ""

  override def getUsername: String = if (saveUserCheckBox.isChecked) userEditText.getText.toString else ""

  override def isLoginOffline: Boolean = offlineLoginCheckBox.isChecked

  override def showLoginOffline(isEnable: Boolean): Unit = offlineLoginCheckBox.setChecked(isEnable)

  override def showSaveUsername(isEnable: Boolean): Unit = saveUserCheckBox.setChecked(isEnable)

  override def showSavePassword(isEnable: Boolean): Unit = savePassCheckBox.setChecked(isEnable)

  override def getCurrentAppVersion: String = getPackageManager.getPackageInfo(ctx.getPackageName, 0).versionName
}