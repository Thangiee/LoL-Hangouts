package com.thangiee.LoLHangouts

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.{Looper, Handler}
import android.support.v7.app.ActionBarActivity
import android.util.TypedValue
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
  }

  implicit class RightBiasedEither[A,B](e: Either[A,B]) {
    def map[C](f: B => C) = e.right map f
    def flatMap[C](f: B => Either[A,C]) = e.right flatMap f
  }
}
