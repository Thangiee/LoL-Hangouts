package com.thangiee.LoLHangouts.views

import android.view.View
import android.view.View.OnClickListener
import com.thangiee.LoLHangouts.MyApplication
import org.scaloid.common.{SystemService, TraitView}

trait TView[V <: View] extends TraitView[V] with SystemService {
  def appCtx: MyApplication = context.getApplicationContext.asInstanceOf[MyApplication]

  implicit def function2ViewOnClick(f: View ⇒ Unit): OnClickListener = {
    new OnClickListener {
      override def onClick(v: View): Unit = f.apply(v)
    }
  }
}
