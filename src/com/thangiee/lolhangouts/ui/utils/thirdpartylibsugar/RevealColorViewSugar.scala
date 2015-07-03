package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.animation.{Animator => AndroidAnimator}
import android.content.Context
import android.graphics.Color
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.{GestureDetector, MotionEvent}
import at.markushi.ui.RevealColorView
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.utils._

trait RevealColorViewSugar {

  implicit class RevealColorViewSugar(view: RevealColorView) {
    def ripple(x: Float, y: Float, colorRes: Int = R.color.ripple, duration: Long = 300): Unit = {
      val color = view.getContext.getResources.getColor(colorRes)

      view.reveal(x.toInt, y.toInt, color, 10, duration, new AndroidAnimator.AnimatorListener {
        override def onAnimationEnd(animator: AndroidAnimator): Unit = {
          view.fadeOut(duration)
          delay(duration) {
            // reset the ripple effect
            view.fadeIn(duration = 0)
            view.hide(0, 0, Color.TRANSPARENT, 0, 0, null)
          }
        }

        override def onAnimationStart(animator: AndroidAnimator): Unit = {}
        override def onAnimationRepeat(animator: AndroidAnimator): Unit = {}
        override def onAnimationCancel(animator: AndroidAnimator): Unit = {}
      })
    }
  }

  case class GestureDetectorBuilder() {
    private var longPressListener: Option[MotionEvent => Unit] = None
    private var singleTapUpListener: Option[MotionEvent => Unit] = None

    def onLongPress(f: MotionEvent => Unit): GestureDetectorBuilder = { longPressListener = Some(f); this }
    def onSingleTapUp(f: MotionEvent => Unit): GestureDetectorBuilder = { singleTapUpListener = Some(f); this }

    def build(implicit ctx: Context): GestureDetector = new GestureDetector(ctx, new SimpleOnGestureListener() {
      override def onSingleTapUp(e: MotionEvent): Boolean = { singleTapUpListener.foreach(f => f(e)); false }
      override def onLongPress(e: MotionEvent): Unit = longPressListener.foreach(f => f(e))
    })
  }
}

object RevealColorViewSugar extends RevealColorViewSugar
