package com.thangiee.LoLHangouts

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.{Looper, Handler}
import org.scaloid.common.SystemService

import scala.util.Try

package object utils extends SystemService with Helpers with Implicits {
  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val uiThread = Looper.getMainLooper.getThread

  def appCtx(implicit ctx: Context): MyApplication = ctx.getApplicationContext.asInstanceOf[MyApplication]

  def runOnUiThread[T >: Null](f: => T): T = {
    if (uiThread == Thread.currentThread) {
      f
    } else {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })
      null
    }
  }

  implicit class Asset2Drawable(assetFile: AssetFile)(implicit ctx: Context) {
    def toDrawable: Drawable = Try(Drawable.createFromStream(ctx.getAssets.open(assetFile.path), null))
      .getOrElse(ctx.getResources.getDrawable(R.drawable.ic_load_unknown))
  }
}
