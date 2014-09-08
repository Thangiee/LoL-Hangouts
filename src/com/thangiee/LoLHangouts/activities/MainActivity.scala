package com.thangiee.LoLHangouts.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.{DialogInterface, Intent}
import android.os.{Bundle, Handler, SystemClock}
import android.view.{Menu, MenuItem, ViewGroup, Window}
import android.widget.LinearLayout
import com.anjlab.android.iab.v3.{TransactionDetails, BillingProcessor}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.fragments.ChatScreenFragment
import com.thangiee.LoLHangouts.receivers.DeleteOldMsgReceiver
import com.thangiee.LoLHangouts.services.LoLHangoutsService
import com.thangiee.LoLHangouts.utils.CacheUtils
import com.thangiee.LoLHangouts.utils.Events.FinishMainActivity
import com.thangiee.LoLHangouts.views.SideDrawerView
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Configuration, Style}
import fr.nicolaspomepuy.discreetapprate.{AppRate, RetryPolicy}
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
  override val AD_UNIT_ID: String = "ca-app-pub-4297755621988601/3100022376"

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_screen)
    EventBus.getDefault.register(this)

    // make sure is connected or else go back to login screen
    if (!LoLChat.isConnected) {
      startActivity[LoginActivity]
      finish()
      return
    }

    if (Prefs.getBoolean("offline-login", false)) LoLChat.appearOffline() else LoLChat.appearOnline()
    startService[LoLHangoutsService]
    notificationManager.cancelAll() // clear any left over notification

    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(new SideDrawerView())
    sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    sideDrawer.setDrawerIndicatorEnabled(true)

    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
    setUpFirstTimeLaunch()

    if (savedInstanceState != null){
      val contentFrag = getFragmentManager.getFragment(savedInstanceState, "contentFrag")
      getFragmentManager.beginTransaction().replace(R.id.screen_container, contentFrag).commit()
      appCtx.activeFriendChat = ""
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
      case android.R.id.home  ⇒ if (!appCtx.isChatOpen) sideDrawer.toggleMenu()
      case R.id.menu_about    ⇒ startActivity[AboutActivity]
      case R.id.menu_changelog ⇒ showChangeLog()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

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

  private def cleanUpAndDisconnect() {
    info("[*]cleaning up and disconnecting...")
    EventBus.getDefault.unregister(this, classOf[FinishMainActivity])
    CacheUtils.cleanUp()
    stopService[LoLHangoutsService]
    appCtx.resetState()
    Future (LoLChat.disconnect())
  }

  override def onBackPressed(): Unit = {
    // if in chat panel slide back to the friend list panel
    if (appCtx.isChatOpen) {
      val chatScreenFragment = getFragmentManager.findFragmentById(R.id.screen_container).asInstanceOf[ChatScreenFragment]
      chatScreenFragment.slidingLayout.openPane()
      return
    }

    // exit the app after quickly double clicking the back button
    if (doubleBackToExitPressedOnce) {
      cleanUpAndDisconnect()
      super.onBackPressed()
      return
    }

    doubleBackToExitPressedOnce = true
    toast(R.string.back_to_exit.r2String)
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

  override def onProductPurchased(productId: String, details: TransactionDetails): Unit = {
    info("[+] Product purchased: " + productId)
    Prefs.putBoolean("is_ads_enable", false)
    R.string.ads_disabled.r2String.makeCrouton(Style.CONFIRM, Configuration.DURATION_LONG)
    bp.release()
  }

  override def onBillingInitialized(): Unit = {
    info("[*] Billing Initialized")
    if (bp.listOwnedProducts.contains(SKU_REMOVE_ADS)) {
      R.string.ads_already_disabled.r2String.makeCrouton(Style.INFO)
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
      R.string.ads_disabled.r2String.makeCrouton(Style.CONFIRM, Configuration.DURATION_LONG)
      bp.release()
    }
  }

  override def onBillingError(errorCode: Int, error: Throwable): Unit = {
    warn("[!] Billing Error: " + errorCode)
    bp.release()
  }

  def onEvent(event: FinishMainActivity): Unit = {
    cleanUpAndDisconnect()
    finish()
  }
}