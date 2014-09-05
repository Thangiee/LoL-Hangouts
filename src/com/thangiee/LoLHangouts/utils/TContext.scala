package com.thangiee.LoLHangouts.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.{BitmapFactory, Bitmap}
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.thangiee.LoLHangouts.{R, MyApplication}
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

  implicit class Asset2Drawable(assetFile: AssetFile) {
    def toDrawable: Drawable = Try(Drawable.createFromStream(appCtx.getAssets.open(assetFile.path), null))
                                .getOrElse(appCtx.getResources.getDrawable(R.drawable.ic_load_error))
  }
}
