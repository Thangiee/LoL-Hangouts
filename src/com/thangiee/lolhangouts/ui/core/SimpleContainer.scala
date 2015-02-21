package com.thangiee.lolhangouts.ui.core

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.thangiee.lolhangouts.ui.utils._

abstract class SimpleContainer(implicit ctx: Context) extends FrameLayout(ctx) with Container {

  def layoutId: Int

  override def getView: View = {
    layoutInflater.inflate(layoutId, null)
  }
}
