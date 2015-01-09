package com.thangiee.LoLHangouts

import android.view.View
import org.scaloid.common.TagUtil

trait Container extends AnyRef with TagUtil {

  def getView: View

  def onBackPressed(): Boolean
}
