package com.thangiee.LoLWithFriends.activities

import android.os.{Bundle, Handler}
import android.view.{Menu, MenuItem}
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.fragments.ChatScreenFragment
import com.thangiee.LoLWithFriends.services.{ChatService, FriendListService}
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

    startService[FriendListService]
    startService[ChatService]

    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(new SideDrawerView())
    sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    sideDrawer.setDrawerIndicatorEnabled(true)

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
    stopService[FriendListService]
    stopService[ChatService]
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
}

