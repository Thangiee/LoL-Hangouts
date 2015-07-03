package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.os.Handler
import android.view.View
import com.daimajia.androidanimations.library.{Techniques, YoYo}
import com.github.nscala_time.time.Imports._
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.Animator.AnimatorListener
import org.scaloid.common.Implicits._

trait AnimationViewSugar {
  implicit class AnimationViewHelper(v: View) {
    private lazy val inListener = new AnimatorListener {
      override def onAnimationEnd(animator: Animator): Unit = {}
      override def onAnimationRepeat(animator: Animator): Unit = {}
      override def onAnimationCancel(animator: Animator): Unit = {}
      override def onAnimationStart(animator: Animator): Unit = {
        new Handler().postDelayed(() => v.setVisibility(View.VISIBLE), animator.getStartDelay + 150)
      }
    }

    def shake(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.Shake, duration, delay).playOn(v)
    def zoomOut(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.ZoomOut, duration, delay).playOn(v)

    def fadeIn(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.FadeIn, duration, delay).withListener(inListener).playOn(v)
    def fadeOut(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.FadeOut, duration, delay).playOn(v)
    def fadeOutUp(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.FadeOutUp, duration, delay).playOn(v)
    def fadeInDown(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.FadeInDown, duration, delay).withListener(inListener).playOn(v)

    def slideOutUp(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideOutUp, duration, delay).playOn(v)
    def slideOutDown(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideOutDown, duration, delay).playOn(v)
    def slideOutLeft(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideOutLeft, duration, delay).playOn(v)
    def slideOutRight(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideOutRight, duration, delay).playOn(v)
    def slideInDown(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideInDown, duration, delay).withListener(inListener).playOn(v)
    def slideInUp(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideInUp, duration, delay).withListener(inListener).playOn(v)
    def slideInLeft(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideInLeft, duration, delay).withListener(inListener).playOn(v)
    def slideInRight(duration: Long = 1.second.millis, delay: Long = 0) = compose(Techniques.SlideInRight, duration, delay).withListener(inListener).playOn(v)

    private def compose(tech: Techniques, dur: Long, del: Long) = YoYo.`with`(tech).duration(dur).delay(del)
  }
}

object AnimationViewSugar extends AnimationViewSugar
