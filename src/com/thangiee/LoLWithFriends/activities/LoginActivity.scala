package com.thangiee.LoLWithFriends.activities

import android.os.{SystemClock, Bundle}
import android.widget.EditText
import com.dd.CircularProgressButton
import com.thangiee.LoLWithFriends.{MyApp, R}
import com.thangiee.LoLWithFriends.api.LoLChat
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginActivity extends SActivity with UpButton {
  lazy val userEditText = find[EditText](R.id.et_username)
  lazy val passwordEditText = find[EditText](R.id.et_password)
  lazy val logInButton = find[CircularProgressButton](R.id.btn_login).onClick(login())

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)
    setTitle(MyApp.selectedServer.name)
    getActionBar.setIcon(MyApp.selectedServer.flag)
    logInButton.setIndeterminateProgressMode(true)
  }

  private def login() {
    if (logInButton.getProgress == -1) { logInButton.setProgress(0); return } // when button in fail state, switch to default state
    logInButton.disable // disable to prevent multiple login attempt
    Future {
      runOnUiThread(logInButton.setProgress(50))
      SystemClock.sleep(500)

      // try to connect to server and warn the user if fail to connect
      if (!LoLChat.connect(MyApp.selectedServer.url)) {
        runOnUiThread(Crouton.makeText(this, "Fail to connect to server", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
        logInButton.enable
        return
      }

      SystemClock.sleep(500)

      // after successfully connecting to server, try to login
      if (LoLChat.login("thangiee", "Eequalsmc2")) {
        runOnUiThread(logInButton.setProgress(100))
        MyApp.currentUser = "thangiee"
        startActivity[MainActivity]
        getParent.finish()
        finish()
      } else {
        runOnUiThread(Crouton.makeText(this, "Invalid username/passwoard", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
      }
      logInButton.enable
    }
  }
}
