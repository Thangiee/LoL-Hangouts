package com.thangiee.LoLHangouts.views

import android.view.View
import com.thangiee.LoLHangouts.MyApplication
import org.scaloid.common.{InterfaceImplicits, SystemService, TraitView}

trait TView[V <: View] extends TraitView[V] with SystemService with InterfaceImplicits {
  def appCtx: MyApplication = context.getApplicationContext.asInstanceOf[MyApplication]
}
