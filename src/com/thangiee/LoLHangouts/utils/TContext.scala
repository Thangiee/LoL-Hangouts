package com.thangiee.LoLHangouts.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.{Bitmap, BitmapFactory}
import android.preference.PreferenceManager
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.thangiee.LoLHangouts.{MyApplication, R}
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

import scala.util.Try

trait TContext {
  implicit val ctx: Context

  def appCtx: MyApplication = ctx.getApplicationContext.asInstanceOf[MyApplication]

  implicit def function2OnCheckedChangeListener[F](f: (CompoundButton, Boolean) ⇒ F): OnCheckedChangeListener = {
    new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean): Unit = f(buttonView, isChecked)
    }
  }

  implicit def function2Runnable[F](f: => F) = new Runnable() { def run() { f } }

  implicit class Drawable2Bitmap(drawableId: Int) {
    def toBitmap: Bitmap = BitmapFactory.decodeResource(ctx.getResources, drawableId)
  }

  implicit class String2Crouton(string: String) {
    def makeCrouton(style: Style = Style.ALERT, duration: Int = Configuration.DURATION_SHORT) {
      ctx match {
        case activity: Activity ⇒ Crouton.makeText(activity, string, style)
                                    .setConfiguration(new Configuration.Builder().setDuration(duration).build()).show()
        case _ ⇒  println("[!] Cant make Crouton. Context is not instance of Activity.")
      }
    }
  }

  implicit class Res2Pref(resId: Int) {
    private val key = ctx.getResources.getString(resId)
    def pref2Int(default: Int): Int = PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, default.toString).toInt
    def pref2Long(default: Long): Long = PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, default.toString).toLong
    def pref2Float(default: Float): Float = PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, default.toString).toFloat
    def pref2Boolean(default: Boolean = false): Boolean = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(key, default)
    def pref2String(default: String): String = PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, default)
  }

  implicit class Asset2Drawable(assetFile: AssetFile) {
    def toDrawable: Drawable = Try(Drawable.createFromStream(appCtx.getAssets.open(assetFile.path), null))
                                .getOrElse(appCtx.getResources.getDrawable(R.drawable.ic_load_error))
  }
}
