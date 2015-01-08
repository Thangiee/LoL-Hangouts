package com.thangiee.LoLHangouts

import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.utils._

abstract class SimpleContainer(implicit ctx: Context) extends Container {

  def layoutId: Int

  override def onBackPressed(): Boolean = {
    false
  }
  override def getView: View = {
    layoutInflater.inflate(layoutId, null)
  }
}
