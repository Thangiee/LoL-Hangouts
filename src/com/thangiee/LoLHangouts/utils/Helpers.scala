package com.thangiee.LoLHangouts.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import com.daimajia.androidanimations.library.{Techniques, YoYo}
import com.github.nscala_time.time.Imports._

trait Helpers extends org.scaloid.common.Helpers with ViewHelpers with AnimationHelpers

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

  implicit class BetterTextView(tv: TextView) {
    def txt2str: String = tv.getText.toString
  }
}

trait AnimationHelpers {

  implicit class BetterYoYo(v: View) {
    def shake(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.Shake, duration, delay).playOn(v)
    def fadeOutUp(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.FadeOutUp, duration, delay).playOn(v)

    private def compose(tech: Techniques, dur: Long, del: Long) = YoYo.`with`(tech).duration(dur).delay(del)
  }
}
