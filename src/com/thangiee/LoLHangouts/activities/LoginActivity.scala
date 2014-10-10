package com.thangiee.LoLHangouts.activities

import android.content.DialogInterface
import android.os.{Bundle, SystemClock}
import android.view.{MenuItem, Window}
import android.widget.{ImageView, CheckBox, CompoundButton, EditText}
import com.dd.CircularProgressButton
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.api.utils.{RiotApi, Region}
import play.api.libs.json.Json
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Try, Failure, Success}
import scalaj.http.{HttpOptions, Http}

class LoginActivity extends TActivity with UpButton {
  lazy val userEditText = find[EditText](R.id.et_username)
  lazy val passwordEditText = find[EditText](R.id.et_password)
  lazy val logInButton = find[CircularProgressButton](R.id.btn_login).onClick(login())
  lazy val saveUserCheckBox = find[CheckBox](R.id.cb_save_user)
  lazy val savePassCheckBox = find[CheckBox](R.id.cb_save_pass)
  lazy val offlineLoginCheckBox = find[CheckBox](R.id.cb_offline_login)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)
    logInButton.setIndeterminateProgressMode(true)

    // load account info if saved
    userEditText.setText(Prefs.getString("user", ""))
    passwordEditText.setText(Prefs.getString("pass", ""))

    saveUserCheckBox.setOnCheckedChangeListener((cb: CompoundButton, isChecked: Boolean) ⇒ if (isChecked) saveUser() else clearUser())
    savePassCheckBox.setOnCheckedChangeListener((cb: CompoundButton, isChecked: Boolean) ⇒ if (isChecked) savePass() else clearPass())
    offlineLoginCheckBox.setOnCheckedChangeListener((cb: CompoundButton, isChecked: Boolean) ⇒ offlineLogin(if (isChecked) true else false))

    // check the checkbox if those fields are not empty
    if (userEditText.length() != 0) saveUserCheckBox.setChecked(true)
    if (passwordEditText.length() != 0) savePassCheckBox.setChecked(true)
    if (Prefs.getBoolean("offline-login", false)) offlineLoginCheckBox.setChecked(true)
  }

  override def onResume(): Unit = {
    super.onResume()
    Region.getFromId(Prefs.getString("region-key", "")) match {  // check if a region was previously selected
      case Some(region) ⇒
        appCtx.selectedRegion = region
        setTitle(region.name)
        getActionBar.setIcon(region.flag)

        // check to show change log
        val version = getPackageManager.getPackageInfo(getPackageName, 0).versionName
        if (!Prefs.getString("app_version", "0").equals(version)) { // check if app updated
          showChangeLog() // show change log if updated
          Prefs.putString("app_version", version) // update the stored app version value
        }
      case None ⇒ startActivity[RegionSelectionActivity]; finish()  // otherwise, go to the region selection screen
    }
  }

  override def onStart(): Unit = {
    super.onStart()
    find[ImageView](R.id.img_login_bg).setImageResource(R.drawable.shadow_isles_640)
  }

  override def onStop(): Unit = {
    find[ImageView](R.id.img_login_bg).setImageDrawable(null)
    super.onStop()
  }

  private def login() {
    if (saveUserCheckBox.isChecked) saveUser()
    if (savePassCheckBox.isChecked) savePass()

    if (logInButton.getProgress == -1) {
      logInButton.setProgress(0)
      return
    } // when button in fail state, switch to default state
    //    logInButton.disable // disable to prevent multiple login attempt
    Future {
      runOnUiThread(logInButton.setProgress(50))
      SystemClock.sleep(500)  // give time to animation to animate

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
        appCtx.currentUser = userEditText.getText.toString
        findInGameName()  // try to find in game name in case the login name is different than the in game name
        SystemClock.sleep(150)
        runOnUiThread(logInButton.setProgress(100))
        finish()
        startActivity[MainActivity]
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

  private def saveUser(): Unit = Prefs.putString("user", userEditText.getText.toString)

  private def clearUser(): Unit = Prefs.putString("user", "")

  private def savePass(): Unit = Prefs.putString("pass", passwordEditText.getText.toString)

  private def clearPass(): Unit = Prefs.putString("pass", "")

  private def offlineLogin(boolean: Boolean): Unit = Prefs.putBoolean("offline-login", boolean)

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

  private def findInGameName(): Unit = {
    val cacheName = Prefs.getString("cache-" + appCtx.currentUser.toLowerCase, "")

    if (cacheName.isEmpty) {
      info("[-] cache name miss")
      RiotApi.setRegion(appCtx.selectedRegion.id)
      RiotApi.getSummonerName(LoLChat.summonerId().getOrElse("0")) match {
        case Some(name) =>
          Prefs.putString("cache-" + appCtx.currentUser.toLowerCase, name)
          appCtx.currentUser = name
        case None =>
          warn("[!] Something when wrong with the response, didn't find name.")
      }
    } else {
      info("[+] cache name hit")
      appCtx.currentUser = cacheName
    }
  }
}
