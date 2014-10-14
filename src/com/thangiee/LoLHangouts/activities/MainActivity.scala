package com.thangiee.LoLHangouts.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, SystemClock}
import android.view.{Menu, MenuItem, ViewGroup}
import android.widget.LinearLayout
import com.anjlab.android.iab.v3.{BillingProcessor, TransactionDetails}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.fragments.ChatScreenFragment
import com.thangiee.LoLHangouts.receivers.DeleteOldMsgReceiver
import com.thangiee.LoLHangouts.services.LoLHangoutsService
import com.thangiee.LoLHangouts.utils.Events.FinishMainActivity
import com.thangiee.LoLHangouts.views.SideDrawerView
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Configuration, Style}
import fr.nicolaspomepuy.discreetapprate.{AppRate, RetryPolicy}
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common._

class MainActivity extends TActivity with Ads with BillingProcessor.IBillingHandler {
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
      getFragmentManager.beginTransaction().add(R.id.screen_container, ChatScreenFragment()).commit()
    }
    rateMyApp()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    if (!bp.handleActivityResult(requestCode, resultCode, data))
      super.onActivityResult(requestCode, resultCode, data)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.overflow, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => if (!appCtx.isChatOpen) sideDrawer.toggleMenu(); true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onBackPressed(): Unit = {
    // if in chat panel slide back to the friend list panel
    if (appCtx.isChatOpen) {
      val chatScreenFragment = getFragmentManager.findFragmentById(R.id.screen_container).asInstanceOf[ChatScreenFragment]
      chatScreenFragment.slidingLayout.openPane()
      return
    }

    // go back to home screen
    val homeScreen = new Intent(Intent.ACTION_MAIN)
    homeScreen.addCategory(Intent.CATEGORY_HOME)
    homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(homeScreen)
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