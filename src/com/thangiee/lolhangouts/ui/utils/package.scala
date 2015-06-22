package com.thangiee.lolhangouts.ui

import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.{Handler, Looper}
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import com.thangiee.lolhangouts.MyApplication
import org.scaloid.common.SystemServices

package object utils extends SystemServices with Helpers with Implicits with Logger {
  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val uiThread = Looper.getMainLooper.getThread

  private[lolhangouts] val Good = org.scalactic.Good
  private[lolhangouts] val Bad = org.scalactic.Bad

  def appCtx(implicit ctx: Context): MyApplication = ctx.getApplicationContext.asInstanceOf[MyApplication]

  def runOnUiThread(f: => Unit): Unit = {
    if (uiThread == Thread.currentThread()) f
    else handler.post(new Runnable { def run(): Unit = f})
  }

  def toolbarHeight(implicit ctx: Context): Int = {
    val tv = new TypedValue()
    if (ctx.getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      TypedValue.complexToDimensionPixelSize(tv.data, ctx.getResources.getDisplayMetrics)
    } else {
      0
    }
  }

  def delay(mills: Long)(f: => Unit): Unit = {
    val handler = new Handler()
    handler.postDelayed(() => f, mills)
  }
  
  def api_=(targetVersion: Int)(f: => Unit): Unit = {
    if (android.os.Build.VERSION.SDK_INT == targetVersion) f
  }

  def api_>=(targetVersion: Int)(f: => Unit): Unit = {
    if (android.os.Build.VERSION.SDK_INT >= targetVersion) f
  }

  def api_<=(targetVersion: Int)(f: => Unit): Unit = {
    if (android.os.Build.VERSION.SDK_INT <= targetVersion) f
  }

  def hasWifiConnection(implicit ctx: Context): Boolean = {
    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected
  }

  def setToolbarTitle(title: String)(implicit ctx: Context): Unit = {
    ctx.asInstanceOf[AppCompatActivity].getSupportActionBar.setTitle(title)
  }

  def setToolbarSubTitle(subTitle: String)(implicit ctx: Context): Unit = {
    ctx.asInstanceOf[AppCompatActivity].getSupportActionBar.setSubtitle(subTitle)
  }

  def isAppInForeground(implicit ctx: Context): Boolean = {
    val tasks = activityManager.getRunningTasks(Integer.MAX_VALUE)
    tasks.get(0).topActivity.getPackageName == appCtx.getPackageName
  }

  def screenAbsWidth(implicit ctx: Context): Int = {
    val display = ctx.asInstanceOf[AppCompatActivity].getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    if (ctx.getResources.getConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) size.x else size.y
  }

  def screenAbsHeight(implicit ctx: Context): Int = {
    val display = ctx.asInstanceOf[AppCompatActivity].getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    if (ctx.getResources.getConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) size.y else size.x
  }

  implicit class Rounding(number: Double) {
    def roundTo(DecimalPlace: Int): Double = {
      if (number.isNaN) return 0.0

      // need to use Locale.US otherwise this throw NumberFormatException: Invalid double
      // on phones that are set on a language that use comma to denote decimal
      new DecimalFormat("###." + ("#" * DecimalPlace), new DecimalFormatSymbols(Locale.US)).format(number).toDouble
    }
  }
}
