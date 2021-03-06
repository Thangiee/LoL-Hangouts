package com.thangiee.lolhangouts.ui

import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.{BitmapDrawable, Drawable}
import android.graphics.{Bitmap, BitmapFactory, Point, Typeface}
import android.net.ConnectivityManager
import android.os.{Handler, Looper}
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.{ImageView, CompoundButton, TextView}
import com.squareup.picasso.Picasso
import com.thangiee.lolhangouts.ui.core.TActivity
import com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar._
import com.thangiee.lolhangouts.{MyApplication, R}
import org.scaloid.common.{Helpers, Implicits, SystemServices}

import scala.language.implicitConversions
import scala.util.Try

package object utils extends SystemServices with Sugar with Helpers with Implicits with Logger {
  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val uiThread = Looper.getMainLooper.getThread

  private[lolhangouts] val Good = org.scalactic.Good
  private[lolhangouts] val Bad = org.scalactic.Bad

  def appCtx(implicit ctx: Context): MyApplication = ctx.getApplicationContext.asInstanceOf[MyApplication]

  def runOnUiThread(f: => Unit): Unit = {
    if (uiThread == Thread.currentThread()) f
    else handler.post(() => f)
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
    handler.postDelayed(f, mills)
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
    val display = windowManager.getDefaultDisplay
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

  implicit def func2OnCheckedChangeListener[F](f: (CompoundButton, Boolean) ⇒ F): OnCheckedChangeListener =
    (buttonView: CompoundButton, isChecked: Boolean) => f(buttonView, isChecked)

  implicit class Rounding(number: Double) {
    def roundTo(DecimalPlace: Int): Double = {
      if (number.isNaN) return 0.0

      // need to use Locale.US otherwise this throw NumberFormatException: Invalid double
      // on phones that are set on a language that use comma to denote decimal
      new DecimalFormat("###." + ("#" * DecimalPlace), new DecimalFormatSymbols(Locale.US)).format(number).toDouble
    }
  }

  implicit class ViewSugar(v: View) {
    def setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bot: Int = 0) = {
      v.getLayoutParams match {
        case p: MarginLayoutParams =>
          p.setMargins(left, top, right, bot)
          v.requestLayout()
        case _                     =>
      }
    }
  }

  implicit class ImageViewSugar(iv: ImageView) {
    def setImageDrawable(imageFile: ImageFile)(implicit ctx: Context): ImageView = {
      Picasso.`with`(ctx).load(imageFile.path).placeholder(imageFile.defaultDrawableId).into(iv)
      iv
    }
  }

  implicit class TextViewSugar(tv: TextView) {
    def txt2str: String = tv.getText.toString
  }

  implicit class DrawableSugar(drawableId: Int)(implicit ctx: Context) {
    def toBitmap: Bitmap = BitmapFactory.decodeResource(ctx.getResources, drawableId)
  }

  implicit class BitMapSugar(bitmap: Bitmap)(implicit ctx: Context) {
    def toDrawable: Drawable = new BitmapDrawable(ctx.getResources, bitmap)
  }

  implicit class RichSnackBar(snackbar: Snackbar) {
    def setAction(text: CharSequence, onAction: => Unit): Snackbar =
      snackbar.setAction(text, (v: View) => {onAction; snackbar.dismiss()})
  }

  object SnackBar {
    def apply(viewId: Int, resId: Int)(implicit ctx: Context): Snackbar = apply(viewId, resId.r2String)(ctx)
    def apply(text: String)(implicit ctx: Context): Snackbar = apply(ctx.asInstanceOf[TActivity].snackBarHolderId(), text)
    def apply(viewId: Int, text: String)(implicit ctx: Context): Snackbar = {
      val sb = Snackbar.make(ctx.asInstanceOf[TActivity].find[View](viewId), text, Snackbar.LENGTH_LONG)
      sb.setAction(R.string.dismiss, (v: Snackbar) => v.dismiss())
    }
  }
}
