package com.thangiee.LoLHangouts.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams

trait Helpers extends org.scaloid.common.Helpers with ViewHelpers

object Helpers extends Helpers

trait ViewHelpers {
  implicit class BetterView(v: View) {
    def setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bot: Int = 0) = {
      v.getLayoutParams match {
        case p: MarginLayoutParams =>
          p.setMargins(left, top, right, bot)
          v.requestLayout()
        case _ =>
      }
    }
  }
}