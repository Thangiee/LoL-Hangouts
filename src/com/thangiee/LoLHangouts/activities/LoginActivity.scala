package com.thangiee.LoLHangouts.activities

import android.content.DialogInterface
import android.os.{Bundle, SystemClock}
import android.view.{MenuItem, Window}
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.{CheckBox, CompoundButton, EditText}
import com.dd.CircularProgressButton
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.{Region, LoLChat}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginActivity extends TActivity with UpButton {
  lazy val userEditText = find[EditText](R.id.et_username)
  lazy val passwordEditText = find[EditText](R.id.et_password)
  lazy val logInButton = find[CircularProgressButton](R.id.btn_login).onClick(login())
  lazy val rememberCheckBox = find[CheckBox](R.id.cb_remember_account)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)
    logInButton.setIndeterminateProgressMode(true)

    // load account info if saved
    userEditText.setText(Prefs.getString("user", ""))
    passwordEditText.setText(Prefs.getString("pass", ""))

    rememberCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) saveUserAndPass() else clearUserAndPass()
      }
    })

    // check the checkbox if those fields are not empty
    if (List(userEditText, passwordEditText).forall(_.length() != 0)) rememberCheckBox.setChecked(true)

    val version = getPackageManager.getPackageInfo(getPackageName, 0).versionName
    if (!Prefs.getString("app_version", "0").equals(version)) { // check if app updated
      showChangeLog()                           // show change log if updated
      Prefs.putString("app_version", version)   // update the stored app version value
    }
  }

  override def onResume(): Unit = {
    super.onResume()
    Region.getFromString(Prefs.getString("region-key", "")) match {  // check if a region was previously selected
      case Some(region) ⇒
        appCtx.selectedRegion = region
        setTitle(region.name)
        getActionBar.setIcon(region.flag)
      case None ⇒ startActivity[RegionSelectionActivity]; finish()  // otherwise, go to the region selection screen
    }
  }

  private def login() {
    if (rememberCheckBox.isChecked) saveUserAndPass()

    if (logInButton.getProgress == -1) {
      logInButton.setProgress(0)
      return
    } // when button in fail state, switch to default state
    //    logInButton.disable // disable to prevent multiple login attempt
    Future {
      runOnUiThread(logInButton.setProgress(50))
      SystemClock.sleep(500)

      // try to connect to server and warn the user if fail to connect
      if (!LoLChat.connect(appCtx.selectedRegion.url)) {
        runOnUiThread("Fail to connect to server".makeCrouton())
        runOnUiThread(logInButton.setProgress(-1))
        //        logInButton.enable
        return
      }

      SystemClock.sleep(500)

      // after successfully connecting to server, try to login
      if (LoLChat.login(userEditText.getText.toString, passwordEditText.getText.toString)) {
        runOnUiThread(logInButton.setProgress(100))
        appCtx.currentUser = userEditText.getText.toString
        startActivity[MainActivity]
        finish()
      } else {
        runOnUiThread("Invalid username/password".makeCrouton())
        runOnUiThread(logInButton.setProgress(-1))
      }
      //      logInButton.enable
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home ⇒ startActivity[RegionSelectionActivity]; true
      case _                 ⇒ super.onOptionsItemSelected(item)
    }
  }

  private def saveUserAndPass() {
    List(("user", userEditText), ("pass", passwordEditText)).map(p => Prefs.putString(p._1, p._2.getText.toString))
  }

  private def clearUserAndPass() = List(("user", ""), ("pass", "")).map(p => Prefs.putString(p._1, p._2))

  private def showChangeLog(): Unit = {
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
}
