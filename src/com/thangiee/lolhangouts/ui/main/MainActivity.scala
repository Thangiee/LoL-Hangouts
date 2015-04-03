package com.thangiee.lolhangouts.ui.main

import android.content.{Context, Intent}
import android.os.Bundle
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener
import android.view.View.OnClickListener
import android.view.{Menu, MenuItem, View, ViewGroup}
import android.widget.LinearLayout
import com.anjlab.android.iab.v3.{BillingProcessor, TransactionDetails}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.lolhangouts.data.usecases.GetUserUseCaseImpl
import com.thangiee.lolhangouts.ui.core._
import com.thangiee.lolhangouts.ui.friendchat.ChatContainer
import com.thangiee.lolhangouts.ui.livegame.ViewGameScouterActivity
import com.thangiee.lolhangouts.ui.profile.{ProfileContainer, ViewProfileActivity}
import com.thangiee.lolhangouts.ui.services.LoLHangoutsService
import com.thangiee.lolhangouts.ui.sidedrawer.{DrawerItem, SideDrawerView}
import com.thangiee.lolhangouts.ui.utils.Events.SwitchContainer
import com.thangiee.lolhangouts.ui.utils._
import com.thangiee.lolhangouts.{MyApplication, R}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.Configuration
import fr.nicolaspomepuy.discreetapprate.{AppRate, RetryPolicy}

import scala.concurrent.ExecutionContext.Implicits.global

class MainActivity extends TActivity with Ads with BillingProcessor.IBillingHandler {
  lazy val contentContainer = find[LinearLayout](R.id.content_container)
  lazy val sideDrawerView   = find[SideDrawerView](R.id.drawer_layout)
  lazy val toolbarShadow    = find[View](R.id.toolbar_shadow)

  val SKU_REMOVE_ADS              = "lolhangouts.remove.ads"
  var bp       : BillingProcessor = _
  var container: Container        = _

  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.ads_holder)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/1893861576"
  override      val layoutId              = R.layout.act_main_screen

  private lazy val isGuestMode       = getIntent.getBooleanExtra("is-guest-mode-key", false)
  private lazy val loadUser          = GetUserUseCaseImpl().loadUser()
  private      var switchContainer   = false  // indicate when the current container needs to be replace
  private      var showToolBarShadow = true

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    EventBus.getDefault.registerSticky(this)

    if (!isGuestMode) {
      container = new ChatContainer()
      startService[LoLHangoutsService]
    } else {
      container = new SimpleContainer() { override val layoutId: Int = R.layout.guest_mode_screen }
    }
    contentContainer.addView(container.getView)

    // wait til drawer close animation complete before changing container to avoid UI lag
    find[SideDrawerView](R.id.drawer_layout).onDrawerClosed { drawer =>
      if (switchContainer) {
        contentContainer.removeAllViews()
        contentContainer.addView(container.getView)
        invalidateOptionsMenu()
        switchContainer = false
        toolbarShadow.setVisibility(if (showToolBarShadow) View.VISIBLE else View.INVISIBLE)
      }
    }

    // if the container did not handle the nav icon click event then open the drawer
    toolbar.setNavigationOnClickListener(new OnClickListener {
      def onClick(view: View): Unit = if (!container.onNavIconClick()) sideDrawerView.openDrawer()
    })

    notificationManager.cancelAll() // clear any left over notification
    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
    rateMyApp()
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.removeAllStickyEvents()
    EventBus.getDefault.unregister(this)
    super.onDestroy()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    if (!bp.handleActivityResult(requestCode, resultCode, data))
      super.onActivityResult(requestCode, resultCode, data)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    if (container.onCreateOptionsMenu(getMenuInflater, menu)) true
    else super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (container.onOptionsItemSelected(item)) true
    else super.onOptionsItemSelected(item)
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

  private def rateMyApp(): Unit = {
    AppRate.`with`(this).text(R.string.ask_rate_app.r2String)
      .initialLaunchCount(4)
      .retryPolicy(RetryPolicy.EXPONENTIAL)
      .checkAndShow()
  }

  def setUpBilling(): Unit = {
    bp = new BillingProcessor(ctx, MyApplication.PLAY_SERVICE_KEY, this)
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

  def onEvent(event: SwitchContainer): Unit = runOnUiThread {
    if (event.drawerTitle == DrawerItem.RemoveAds) {
      setUpBilling()
      return
    }

    showToolBarShadow = true // reset shadow
    switchContainer = true   // container need switching
    event.drawerTitle match {
      case DrawerItem.Chat        => switchToChat()
      case DrawerItem.Profile     => switchToMyProfile()
      case DrawerItem.GameScouter => switchToGameScouter()
      case DrawerItem.Search      => switchToProfileSearcher()
    }
  }

  private def switchToChat(): Unit = container = new ChatContainer()

  private def switchToMyProfile(): Unit = {
    loadUser.onSuccess {
      case user => runOnUiThread {
        showToolBarShadow = false // remove the shadow since profile already has shadow under the tabs
        container = new ProfileContainer(user.inGameName, user.region.id)
      }
    }
  }

  private def switchToGameScouter(): Unit = {
    container = new SearchContainer(R.layout.search_container_game_scouter) {
      override def onSearchCompleted(query: String, region: String): Unit = {
        startActivity(ViewGameScouterActivity(query, region))
      }
    }
  }

  private def switchToProfileSearcher(): Unit = {
    container = new SearchContainer(R.layout.search_container_profile) {
      override def onSearchCompleted(query: String, region: String): Unit = {
        startActivity(ViewProfileActivity(query, region))
      }
    }
  }
}

object MainActivity extends TIntent {
  def apply(isGuestMode: Boolean)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[MainActivity]).args("is-guest-mode-key" → isGuestMode)
  }
}