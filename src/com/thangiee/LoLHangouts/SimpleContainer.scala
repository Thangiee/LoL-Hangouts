package com.thangiee.LoLHangouts

import android.content.Context
import android.view.{MenuInflater, Menu, View}
import android.widget.FrameLayout
import com.thangiee.LoLHangouts.utils._

abstract class SimpleContainer(implicit ctx: Context) extends FrameLayout(ctx) with Container {

  def layoutId: Int

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    false
  }

  override def onNavIconClick(): Boolean = {
    false
  }

  override def onBackPressed(): Boolean = {
    false
  }
  override def getView: View = {
    layoutInflater.inflate(layoutId, null)
  }
}
