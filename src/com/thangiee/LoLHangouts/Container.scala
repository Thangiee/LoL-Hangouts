package com.thangiee.LoLHangouts

import android.view.View
import com.thangiee.LoLHangouts.domain.utils.TagUtil

trait Container extends AnyRef with TagUtil {

  def getView: View

  def onBackPressed(): Boolean
}
