package com.thangiee.lolhangouts.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.{BitmapFactory, Bitmap, Point}
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.{Looper, Handler}
import android.support.v7.app.ActionBarActivity
import android.util.TypedValue
import com.thangiee.lolhangouts.{R, MyApplication}
import org.scaloid.common.SystemServices

import scala.util.Try

package object utils extends SystemServices with Helpers with Implicits with Logger {
  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val uiThread = Looper.getMainLooper.getThread

  def appCtx(implicit ctx: Context): MyApplication = ctx.getApplicationContext.asInstanceOf[MyApplication]

  def runOnUiThread[T >: Null](f: => T): T = {
    if (uiThread == Thread.currentThread) {
      f
    } else {
      handler.post(new Runnable { def run(): Unit = f })
      null
    }
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
    ctx.asInstanceOf[ActionBarActivity].getSupportActionBar.setTitle(title)
  }

  def setToolbarSubTitle(subTitle: String)(implicit ctx: Context): Unit = {
    ctx.asInstanceOf[ActionBarActivity].getSupportActionBar.setSubtitle(subTitle)
  }

  def isAppInForeground(implicit ctx: Context): Boolean = {
    val tasks = activityManager.getRunningTasks(Integer.MAX_VALUE)
    tasks.get(0).topActivity.getPackageName == appCtx.getPackageName
  }

  def screenAbsWidth(implicit ctx: Context): Int = {
    val display = ctx.asInstanceOf[ActionBarActivity].getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    if (ctx.getResources.getConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) size.x else size.y
  }

  def screenAbsHeight(implicit ctx: Context): Int = {
    val display = ctx.asInstanceOf[ActionBarActivity].getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    if (ctx.getResources.getConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) size.y else size.x
  }

  implicit class Asset2Drawable(assetFile: AssetFile)(implicit ctx: Context) {
    def toDrawable: Drawable = Try(Drawable.createFromStream(ctx.getAssets.open(assetFile.path), null))
      .getOrElse(ctx.getResources.getDrawable(R.drawable.ic_load_unknown))

    def toBitmap: Bitmap = Try(BitmapFactory.decodeStream(ctx.getAssets.open(assetFile.path)))
      .getOrElse(R.drawable.ic_load_unknown.toBitmap)
  }

  implicit class RightBiasedEither[A,B](e: Either[A,B]) {
    def map[C](f: B => C) = e.right map f
    def flatMap[C](f: B => Either[A,C]) = e.right flatMap f
    def get = e.right get
    def getOrElse(f: => B) = e.right getOrElse f
  }
}