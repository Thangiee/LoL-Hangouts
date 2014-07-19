package com.thangiee.LoLWithFriends.activities

import android.os.{SystemClock, Bundle}
import android.widget.EditText
import com.dd.CircularProgressButton
import com.thangiee.LoLWithFriends.R
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
    setTitle(getIntent.getStringExtra("server-name"))
    getActionBar.setIcon(getIntent.getIntExtra("server-flag", 0))
    logInButton.setIndeterminateProgressMode(true)
  }

  private def login() {
    if (logInButton.getProgress == -1) { logInButton.setProgress(0); return }
    Future {
      runOnUiThread(logInButton.setProgress(50))
      SystemClock.sleep(1000)

      // try to connect to server and warn the user if fail to connect
      if (!LoLChat.connect(getIntent.getStringExtra("server-url"))) {
        runOnUiThread(Crouton.makeText(this, "Fail to connect to server", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
        return
      }

      // after successfully connecting to server, try to login
      if (LoLChat.login(userEditText.getText.toString, passwordEditText.getText.toString)) {
        runOnUiThread(logInButton.setProgress(100))
        startActivity[TestActivity]
      } else {
        runOnUiThread(Crouton.makeText(this, "Invalid username/passwoard", Style.ALERT).show())
        runOnUiThread(logInButton.setProgress(-1))
      }
    }
  }
}
