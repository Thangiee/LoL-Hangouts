package com.thangiee.LoLHangouts.login

import android.content.DialogInterface
import android.os.Bundle
import android.view.{MenuItem, View, Window}
import android.widget.{CheckBox, EditText, ImageView}
import com.dd.CircularProgressButton
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{MainActivity, RegionSelectionActivity, TActivity}
import com.thangiee.LoLHangouts.data.repository.{AppDataRepoImpl, UserRepoImpl}
import com.thangiee.LoLHangouts.domain.interactor.{CheckNewVerUseCaseImpl, LoginUseCaseImpl}
import com.thangiee.LoLHangouts.utils._
import org.scaloid.common.AlertDialogBuilder

class LoginActivity extends TActivity with LoginView {
  lazy val userEditText         = find[EditText](R.id.et_username)
  lazy val passwordEditText     = find[EditText](R.id.et_password)
  lazy val logInButton          = find[CircularProgressButton](R.id.btn_login)
  lazy val saveUserCheckBox     = find[CheckBox](R.id.cb_save_user)
  lazy val savePassCheckBox     = find[CheckBox](R.id.cb_save_pass)
  lazy val offlineLoginCheckBox = find[CheckBox](R.id.cb_offline_login)

  implicit val appDataRepo  = AppDataRepoImpl()
  implicit val userRepoImpl = UserRepoImpl()
  override val presenter    = new LoginPresenter(this, CheckNewVerUseCaseImpl(), LoginUseCaseImpl())

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(R.layout.login)

    logInButton.setIndeterminateProgressMode(true)
    logInButton.onClick { v: View =>
      if (logInButton.getProgress == -1) logInButton.setProgress(0)
      else presenter.handleLogin(userEditText.getText.toString, passwordEditText.getText.toString)
    }
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

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        startActivity[RegionSelectionActivity]
        this.finish(); true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def showProgress(): Unit = logInButton.setProgress(50)

  override def hideProgress(): Unit = logInButton.setProgress(0)

  override def showLoginSuccess(): Unit = logInButton.setProgress(100)

  override def showErrorMsg(msg: String): Unit = runOnUiThread {
    msg.croutonWarn()
    logInButton.setProgress(-1)
  }

  override def showChangeLog(): Unit = {
    val changeList = getLayoutInflater.inflate(R.layout.change_log_view, null)
    val dialog = new AlertDialogBuilder()
      .setView(changeList)
      .setPositiveButton(android.R.string.ok, (dialog: DialogInterface) ⇒ dialog.dismiss())
      .create()

    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.show()
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(R.color.my_dark_blue.r2Color)
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(R.color.my_orange.r2Color)
  }

  override def navigateToHome(): Unit = {
    finish()
    startActivity[MainActivity]
  }

  override def navigateBack(): Unit = {
    finish()
    startActivity[RegionSelectionActivity]
  }

  override def setTitle(title: String): Unit = ctx.setTitle(title)

  override def setPassword(password: String): Unit = passwordEditText.setText(password)

  override def setUsername(name: String): Unit = userEditText.setText(name)

  override def getPassword: String = if (savePassCheckBox.isChecked) passwordEditText.getText.toString else ""

  override def getUsername: String = if (saveUserCheckBox.isChecked) userEditText.getText.toString else ""

  override def isLoginOffline: Boolean = offlineLoginCheckBox.isChecked

  override def showLoginOffline(isEnable: Boolean): Unit = offlineLoginCheckBox.setChecked(isEnable)

  override def showSaveUsername(isEnable: Boolean): Unit = saveUserCheckBox.setChecked(isEnable)

  override def showSavePassword(isEnable: Boolean): Unit = savePassCheckBox.setChecked(isEnable)
}