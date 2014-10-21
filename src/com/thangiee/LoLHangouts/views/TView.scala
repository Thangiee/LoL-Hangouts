package com.thangiee.LoLHangouts.views

import android.view.View
import com.thangiee.LoLHangouts.MyApplication
import com.thangiee.LoLHangouts.utils.TCommon
import org.scaloid.common.{InterfaceImplicits, SystemService, TraitView}

trait TView[V <: View] extends TraitView[V] with SystemService with InterfaceImplicits with TCommon {
  def appCtx: MyApplication = context.getApplicationContext.asInstanceOf[MyApplication]
}
