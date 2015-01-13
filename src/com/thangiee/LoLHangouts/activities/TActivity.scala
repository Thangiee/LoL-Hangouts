package com.thangiee.LoLHangouts.activities

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.view._
import android.widget.FrameLayout
import com.balysv.materialmenu.MaterialMenuDrawable
import com.balysv.materialmenu.MaterialMenuDrawable.Stroke
import com.gitonway.lee.niftynotification.lib.{Configuration, Effects, NiftyNotificationView}
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.cache.{MemCache, PrefsCache}
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.services.LoLHangoutsService
import com.thangiee.LoLHangouts.utils.Events._
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus
import org.scaloid.common.{AlertDialogBuilder, SContext, TraitActivity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TActivity extends ActionBarActivity with SContext with TraitActivity[TActivity] {

  override def basis = this
  override implicit val ctx = this

  def layoutId(): Int

  // any class that extends TActivity needs to include a toolbar in its layout
  lazy val toolbar = find[Toolbar](R.id.toolbar)
  lazy val navIcon = new MaterialMenuDrawable(this, Color.YELLOW, Stroke.THIN)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(layoutId())

    if (toolbar != null) {
      setSupportActionBar(toolbar)
      toolbar.setNavigationIcon(navIcon)
    } else {
      error("[!] Can't find toolbar. Make sure to add a toolbar to your layout!")
    }
  }

  override def onResume(): Unit = {
    croutonEventBus.registerSticky(this)
    niftyNotificationEventBus.register(this)
    super.onResume()
  }

  override def onPause(): Unit = {
    croutonEventBus.unregister(this)
    niftyNotificationEventBus.unregister(this)
    super.onPause()
  }

  override def onStop(): Unit = {
    System.gc()
    super.onStop()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.overflow, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_about     => startActivity[AboutActivity]; return true
      case R.id.menu_changelog => showChangeLog(); return true
      case _                   => return false
    }
    super.onOptionsItemSelected(item)
  }

  protected def cleanUpAndDisconnect() {
    info("[*] cleaning up and disconnecting...")
    EventBus.getDefault.unregister(this)
    MemCache.removeAll()
    stopService[LoLHangoutsService]
    appCtx.resetState()
    Future(LoLChat.disconnect())
  }

  protected def showChangeLog(): Unit = {
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

  def onEvent(event: CroutonMsg): Unit = {
    event.msg.makeCrouton(event.style, event.duration).show()
  }

  def onEvent(event: ShowNiftyNotification): Unit = {
    if (find[FrameLayout](R.id.crouton_holder) != null) {
      val cfg = new Configuration.Builder()
        .setAnimDuration(700)
        .setDispalyDuration(3000)
        .setBackgroundColor("#f0022426")
        .setTextColor("#ffbb33")
        .setTextPadding(4) //dp
        .setViewHeight(42) //dp
        .setTextLines(2) //You had better use setViewHeight and setTextLines together
        .setTextGravity(Gravity.CENTER_VERTICAL)
        .build()

      Future {
        val msg = event.msg
        val url = SummonerUtils.profileIconUrl(msg.friendName, PrefsCache.getString(CacheKey.LoginRegionId).getOrElse(""))
        val senderIcon = Picasso.`with`(ctx).load(url).error(R.drawable.ic_load_unknown).get()

        runOnUiThread {
          NiftyNotificationView.build(this, s"${msg.friendName}: ${msg.text}", Effects.thumbSlider, R.id.crouton_holder, cfg)
            .setIcon(new BitmapDrawable(getResources, senderIcon))
            // switch to the sender chat if notification is clicked
            .setOnClickListener((v: View) ⇒ LoLChat.getFriendByName(msg.friendName).map(f => ctx.startActivity(QuickChatActivity(f.name))))
            .show()
        }
      }
    }
  }
}
