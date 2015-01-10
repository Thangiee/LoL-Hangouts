package com.thangiee.LoLHangouts.activities

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, SystemClock}
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import android.widget.LinearLayout
import com.anjlab.android.iab.v3.{BillingProcessor, TransactionDetails}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.receivers.DeleteOldMsgReceiver
import com.thangiee.LoLHangouts.ui.friendchat.ChatContainer
import com.thangiee.LoLHangouts.ui.sidedrawer.{DrawerItem, SideDrawerView}
import com.thangiee.LoLHangouts.utils.Events.{FinishMainActivity, SwitchScreen}
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{Container, R, SimpleContainer}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.Configuration
import fr.nicolaspomepuy.discreetapprate.{AppRate, RetryPolicy}

class MainActivity extends TActivity with Ads with BillingProcessor.IBillingHandler {
  lazy val contentContainer = find[LinearLayout](R.id.content_container)
  lazy val sideDrawerView   = find[SideDrawerView](R.id.drawer_layout)

  var bp       : BillingProcessor = _
  val SKU_REMOVE_ADS              = "lolhangouts.remove.ads"
  var container: Container        = _

  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.linear_layout)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/3100022376"
  override      val layoutId              = R.layout.act_main_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    EventBus.getDefault.register(this)

    container = new ChatContainer()
    contentContainer.addView(container.getView, 1)

    // if the container did not handle the nav icon click event then open the drawer
    toolbar.setNavigationOnClickListener(new OnClickListener {
      def onClick(view: View): Unit = if (!container.onNavIconClick()) sideDrawerView.openDrawer()
    })

    //    if (Prefs.getBoolean("offline-login", false)) LoLChat.appearOffline() else LoLChat.appearOnline()
    //    startService[LoLHangoutsService]
    //    notificationManager.cancelAll() // clear any left over notification
    //
    //
    //    val tv = new TypedValue()
    //    if (getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
    //      val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
    //      sideDrawer.setMenuSize(getScreenWidth - actionBarHeight.toInt)
    //    }
    //
    //    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
    //    setUpFirstTimeLaunch()
    //

    //    rateMyApp()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    if (!bp.handleActivityResult(requestCode, resultCode, data))
      super.onActivityResult(requestCode, resultCode, data)
  }

  override def onBackPressed(): Unit = {
    // check if container handled back press event
    if (container.onBackPressed()) return

    // if not, go back to home screen
    val homeScreen = new Intent(Intent.ACTION_MAIN)
    homeScreen.addCategory(Intent.CATEGORY_HOME)
    homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(homeScreen)
  }

  private def setUpFirstTimeLaunch() {
    if (Prefs.getBoolean("first_launch", true)) {
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
    R.string.ads_disabled.r2String.croutonConfirm(Configuration.DURATION_LONG)
    bp.release()
  }

  override def onBillingInitialized(): Unit = {
    info("[*] Billing Initialized")
    if (bp.listOwnedProducts.contains(SKU_REMOVE_ADS)) {
      R.string.ads_already_disabled.r2String.croutonInfo()
      bp.release()
    } else {
      Prefs.putBoolean("is_ads_enable", true)
      bp.purchase(this, SKU_REMOVE_ADS)
    }
  }

  override def onPurchaseHistoryRestored(): Unit = {
    info("[+] Purchase history restored: " + bp.listOwnedProducts())
    if (bp.listOwnedProducts.contains(SKU_REMOVE_ADS)) {
      Prefs.putBoolean("is_ads_enable", false)
      R.string.ads_disabled.r2String.croutonConfirm(Configuration.DURATION_LONG)
      bp.release()
    }
  }

  override def onBillingError(errorCode: Int, error: Throwable): Unit = {
    warn("[!] Billing Error: " + errorCode)
    bp.release()
  }

  def onEventMainThread(event: SwitchScreen): Unit = {
    if (event.drawerTitle == DrawerItem.RemoveAds) {
      setUpBilling(); return
    }

    container = event.drawerTitle match {
      case DrawerItem.Chat     => new ChatContainer()
      case DrawerItem.LiveGame => new SimpleContainer() { override def layoutId: Int = R.layout.test }
    }

    //todo: functional
    find[SideDrawerView](R.id.drawer_layout).setDrawerListener(new SimpleDrawerListener {
      override def onDrawerClosed(drawerView: View): Unit = {
        // wait til drawer close animation complete before changing view to avoid UI lag
        contentContainer.removeViewAt(1)
        contentContainer.addView(container.getView, 1)
      }
    })
  }

  def onEvent(event: FinishMainActivity): Unit = {
    cleanUpAndDisconnect()
    finish()
  }
}