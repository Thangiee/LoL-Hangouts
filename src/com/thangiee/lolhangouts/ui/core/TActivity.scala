package com.thangiee.lolhangouts.ui.core

import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import android.view._
import android.widget.FrameLayout
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.balysv.materialmenu.MaterialMenuDrawable
import com.balysv.materialmenu.MaterialMenuDrawable.Stroke
import com.gitonway.lee.niftynotification.lib.{Configuration, Effects, NiftyNotificationView}
import com.squareup.picasso.Picasso
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.ui.friendchat.QuickChatActivity
import com.thangiee.lolhangouts.ui.main.AboutActivity
import com.thangiee.lolhangouts.ui.utils.Events._
import com.thangiee.lolhangouts.ui.utils._
import de.keyboardsurfer.android.widget.crouton.{Configuration => Config}
import org.scaloid.common.{SContext, TraitActivity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TActivity extends ActionBarActivity with SContext with TraitActivity[TActivity] {

  override def basis = this
  override implicit val ctx = this

  protected def layoutId(): Int

  // any class that extends TActivity needs to include a toolbar in its layout
  lazy val toolbar = find[Toolbar](R.id.toolbar)
  lazy val navIcon = new MaterialMenuDrawable(this, R.color.abc_secondary_text_material_dark.r2Color, Stroke.THIN)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(layoutId())

    if (toolbar != null) {
      setSupportActionBar(toolbar)
      toolbar.setNavigationIcon(navIcon)
    } else {
      error("[!] Can't find toolbar. Make sure to add a toolbar to your layout!")
    }

    api_>=(LOLLIPOP) {
      // same as <item name="android:windowTranslucentStatus">true</item> but we
      // are able to define the color in <item name="android:statusBarColor">...</item>
      getWindow.getDecorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
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

  protected def showChangeLog(): Unit = {
    val changeList = getLayoutInflater.inflate(R.layout.change_log_view, null)
    new Builder(ctx)
      .title("Change Log")
      .customView(changeList, false)
      .positiveText("Ok")
      .show()
  }

  def onEvent(event: CroutonMsg): Unit = {
    runOnUiThread(event.msg.makeCrouton(event.style, event.duration).show())
  }

  def onEvent(event: ShowNiftyNotification): Unit = {
    if (find[FrameLayout](R.id.crouton_holder) != null) {
      val cfg = new Configuration.Builder()
        .setAnimDuration(700)
        .setDispalyDuration(3000)
        .setBackgroundColor("#f0%06X".format(0xFFFFFF & R.color.primary_dark.r2Color))
        .setTextColor("#%06X".format(0xFFFFFF & R.color.accent.r2Color))
        .setTextPadding(4) //dp
        .setViewHeight(42) //dp
        .setTextLines(2) //You had better use setViewHeight and setTextLines together
        .setTextGravity(Gravity.CENTER_VERTICAL)
        .build()

      Future {
        val msg = event.msg
        val url = SummonerUtils.profileIconUrl(msg.friendName, Cached.loginRegionId.getOrElse(""))
        val senderIcon = Picasso.`with`(ctx).load(url).error(R.drawable.ic_load_unknown).get()

        runOnUiThread {
          NiftyNotificationView.build(this, s"${msg.friendName}: ${msg.text}", Effects.thumbSlider, R.id.crouton_holder, cfg)
            .setIcon(new BitmapDrawable(getResources, senderIcon))
            // switch to the sender chat if notification is clicked
            .setOnClickListener((v: View) ⇒ ctx.startActivity(QuickChatActivity(msg.friendName)))
            .show()
        }
      }
    }
  }

  def onEvent(event: ShowDisconnection): Unit = runOnUiThread {
    R.string.reconnecting.r2String.croutonInfo(Config.DURATION_INFINITE)
  }

  def onEvent(event: FinishActivity): Unit = {
    finish()
  }
}
