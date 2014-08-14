package com.thangiee.LoLWithFriends.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, Handler, SystemClock}
import android.view.{ViewGroup, Menu, MenuItem}
import android.widget.LinearLayout
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

class MainActivity extends SActivity with Ads {
  private var doubleBackToExitPressedOnce = false
  lazy val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)
  override lazy val layout: ViewGroup = find[LinearLayout](R.id.linear_layout)

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_screen)
    LoLChat.appearOnline()
    Prefs.initPrefs(this)

    startService[LoLWithFriendsService]

    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(new SideDrawerView())
    sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    sideDrawer.setDrawerIndicatorEnabled(true)

    setUpFirstTimeLaunch()

    if (savedInstanceState != null){
      val contentFrag = getFragmentManager.getFragment(savedInstanceState, "contentFrag")
      getFragmentManager.beginTransaction().replace(R.id.screen_container, contentFrag).commit()
      MyApp.activeFriendChat = ""
    } else {
      getFragmentManager.beginTransaction().add(R.id.screen_container, new ChatScreenFragment).commit()
    }

    setupAds()
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    val frag = getFragmentManager.findFragmentById(R.id.screen_container)
    getFragmentManager.putFragment(outState, "contentFrag", frag)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.overflow, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_exit     ⇒ cleanUpAndDisconnect(); finish()
      case android.R.id.home  ⇒ if (!MyApp.isChatOpen) sideDrawer.toggleMenu()
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
      alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), TimeUnit.HOURS.toMillis(1), p)

      Prefs.putBoolean("first_launch", false)
    }
  }
}

