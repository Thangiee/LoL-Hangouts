package com.thangiee.LoLHangouts.views

import android.view.View
import com.thangiee.LoLHangouts.MyApplication
import org.scaloid.common.TraitView

trait TView[V <: View] extends TraitView[V]{
  def appCtx: MyApplication = context.getApplicationContext.asInstanceOf[MyApplication]
}
