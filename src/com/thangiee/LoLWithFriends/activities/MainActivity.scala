package com.thangiee.LoLWithFriends.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, Handler, SystemClock}
import android.view.{Menu, MenuItem}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.fragments.ChatScreenFragment
import com.thangiee.LoLWithFriends.receivers.DeleteOldMsgReceiver
import com.thangiee.LoLWithFriends.services.LoLWithFriendsService
import com.thangiee.LoLWithFriends.views.SideDrawerView
import com.thangiee.LoLWithFriends.{MyApp, R}
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MainActivity extends SActivity {
  private var doubleBackToExitPressedOnce = false
  lazy val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(R.layout.main_screen)
    LoLChat.appearOnline()
    Prefs.initPrefs(this)

    startService[LoLWithFriendsService]

    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(new SideDrawerView())
    sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    sideDrawer.setDrawerIndicatorEnabled(true)

    setUpFirstTimeLaunch()
    getFragmentManager.beginTransaction().add(R.id.screen_container, new ChatScreenFragment).commit()
  }

  override def onPause(): Unit = {
    super.onPause()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.exit, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.exit         => cleanUpAndDisconnect(); finish()
      case android.R.id.home => if (!MyApp.isChatOpen) sideDrawer.toggleMenu()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  private def cleanUpAndDisconnect() {
    info("[*]cleaning up and disconnecting...")
    stopService[LoLWithFriendsService]
    MyApp.reset()
    Future {LoLChat.disconnect()}
  }

  // exit the app after quickly double clicking the back button
  override def onBackPressed(): Unit = {
    if (doubleBackToExitPressedOnce) {
      cleanUpAndDisconnect()
      super.onBackPressed()
      return
    }

    doubleBackToExitPressedOnce = true
    toast("Click BACK again to exit")
    new Handler().postDelayed(new Runnable {
      override def run(): Unit = doubleBackToExitPressedOnce = false
    }, 2000)
  }

  private def setUpFirstTimeLaunch() {
    if (Prefs.getBoolean("first_launch", true)) {
      // This will animate the drawer open and closed until the user manually drags it
      sideDrawer.peekDrawer()

      // setup alarm to delete msg older than a time period
      val millis = TimeUnit.DAYS.toMillis(3)
      val i = new Intent(ctx, classOf[DeleteOldMsgReceiver])
      i.putExtra(DeleteOldMsgReceiver.TIME_KEY, millis)
      val p = PendingIntent.getBroadcast(ctx, 0, i, 0)
      alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), millis, p)

      Prefs.putBoolean("first_launch", false)
    }
  }
}

