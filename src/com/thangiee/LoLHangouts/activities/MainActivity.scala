package com.thangiee.LoLHangouts.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, Handler, SystemClock}
import android.view.{Menu, MenuItem, ViewGroup}
import android.widget.LinearLayout
import com.anjlab.android.iab.v3.BillingProcessor
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.api.LoLChat
import com.thangiee.LoLHangouts.fragments.ChatScreenFragment
import com.thangiee.LoLHangouts.receivers.DeleteOldMsgReceiver
import com.thangiee.LoLHangouts.services.LoLWithFriendsService
import com.thangiee.LoLHangouts.views.SideDrawerView
import com.thangiee.LoLHangouts.{MyApp, R}
import de.keyboardsurfer.android.widget.crouton.{Configuration, Style}
import fr.nicolaspomepuy.discreetapprate.{RetryPolicy, AppRate}
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MainActivity extends TActivity with Ads with BillingProcessor.IBillingHandler {
  private var doubleBackToExitPressedOnce = false
  lazy val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)
  var bp: BillingProcessor = _
  val SKU_REMOVE_ADS = "lolhangouts.remove.ads"

  override lazy val layout: ViewGroup = find[LinearLayout](R.id.linear_layout)
  override val AD_UNIT_ID: String = "ca-app-pub-4297755621988601/1349577574"

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

    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
    setUpFirstTimeLaunch()

    if (savedInstanceState != null){
      val contentFrag = getFragmentManager.getFragment(savedInstanceState, "contentFrag")
      getFragmentManager.beginTransaction().replace(R.id.screen_container, contentFrag).commit()
      MyApp.activeFriendChat = ""
    } else {
      getFragmentManager.beginTransaction().add(R.id.screen_container, new ChatScreenFragment).commit()
    }

    rateMyApp()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    if (!bp.handleActivityResult(requestCode, resultCode, data))
      super.onActivityResult(requestCode, resultCode, data)
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
      case R.id.menu_about    ⇒ startActivity[AboutActivity]
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

  private def rateMyApp(): Unit = {
    AppRate.`with`(this).text(R.string.ask_rate_app.r2String)
      .initialLaunchCount(4)
      .retryPolicy(RetryPolicy.EXPONENTIAL)
      .checkAndShow()
  }

  def setUpBilling(): Unit = {
    val key = "google-play-service-key"

    bp = new BillingProcessor(ctx, key, this)
  }

  override def onProductPurchased(productId: String): Unit = {
    info("[+] Product purchased: " + productId)
    Prefs.putBoolean("is_ads_enable", false)
    "Restart app to Disable ads!".makeCrouton(Style.CONFIRM, Configuration.DURATION_LONG)
    bp.release()
  }

  override def onBillingInitialized(): Unit = {
    info("[*] Billing Initialized")
    if (bp.listOwnedProducts.contains(SKU_REMOVE_ADS)) {
      "Ads already removed.".makeCrouton(Style.INFO)
      bp.release()
    } else {
      Prefs.putBoolean("is_ads_enable", true)
      bp.purchase(SKU_REMOVE_ADS)
    }
  }

  override def onPurchaseHistoryRestored(): Unit = {
    info("[+] Purchase history restored: " + bp.listOwnedProducts())
    if (bp.listOwnedProducts.contains(SKU_REMOVE_ADS)) {
      Prefs.putBoolean("is_ads_enable", false)
      "Restart app to Disable ads!".makeCrouton(Style.CONFIRM, Configuration.DURATION_LONG)
      bp.release()
    }
  }

  override def onBillingError(errorCode: Int, error: Throwable): Unit = {
    warn("[!] Billing Error: " + errorCode)
    bp.release()
  }
}

