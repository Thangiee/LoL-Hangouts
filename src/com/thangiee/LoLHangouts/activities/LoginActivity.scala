package com.thangiee.LoLHangouts.activities

import android.os.{Bundle, SystemClock}
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.{CheckBox, CompoundButton, EditText}
import com.dd.CircularProgressButton
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.api.LoLChat
import com.thangiee.LoLHangouts.{MyApp, R}
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginActivity extends SActivity with UpButton {
  lazy val userEditText = find[EditText](R.id.et_username)
  lazy val passwordEditText = find[EditText](R.id.et_password)
  lazy val logInButton = find[CircularProgressButton](R.id.btn_login).onClick(login())
  lazy val rememberCheckBox = find[CheckBox](R.id.cb_remember_account)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)
    setTitle(MyApp.selectedServer.name)
    getActionBar.setIcon(MyApp.selectedServer.flag)
    logInButton.setIndeterminateProgressMode(true)

    Prefs.initPrefs(this)
    userEditText.setText(Prefs.getString("user", ""))
    passwordEditText.setText(Prefs.getString("pass", ""))
    R.string.server_name.r2String

    rememberCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) saveUserAndPass() else clearUserAndPass()
      }
    })
    if (List(userEditText, passwordEditText).forall(_.length() != 0)) rememberCheckBox.setChecked(true)
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
      if (!LoLChat.connect(MyApp.selectedServer.url)) {
        runOnUiThread(Crouton.makeText(this, "Fail to connect to server", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
        //        logInButton.enable
        return
      }

      SystemClock.sleep(500)

      // after successfully connecting to server, try to login
      if (LoLChat.login(userEditText.getText.toString, passwordEditText.getText.toString)) {
        runOnUiThread(logInButton.setProgress(100))
        MyApp.currentUser = userEditText.getText.toString
        startActivity[MainActivity]
        finish()
      } else {
        runOnUiThread(Crouton.makeText(this, "Invalid username/passwoard", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
      }
      //      logInButton.enable
    }
  }

  private def saveUserAndPass() {
    List(("user", userEditText), ("pass", passwordEditText)).map(p => Prefs.putString(p._1, p._2.getText.toString))
  }

  private def clearUserAndPass() = List(("user", ""), ("pass", "")).map(p => Prefs.putString(p._1, p._2))
}
