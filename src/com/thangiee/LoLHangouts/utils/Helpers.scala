package com.thangiee.LoLHangouts.utils

import android.os.Handler
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.{ImageView, TextView}
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback
import com.daimajia.androidanimations.library.{Techniques, YoYo}
import com.github.nscala_time.time.Imports._
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.Animator.AnimatorListener
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import fr.castorflex.android.circularprogressbar.{CircularProgressDrawable, CircularProgressBar}

trait Helpers extends org.scaloid.common.Helpers with ViewHelpers

object Helpers extends Helpers

trait ViewHelpers {

  implicit class ViewHelper(v: View) {
    def setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bot: Int = 0) = {
      v.getLayoutParams match {
        case p: MarginLayoutParams =>
          p.setMargins(left, top, right, bot)
          v.requestLayout()
        case _ =>
      }
    }
  }

  implicit class TextViewHelper(tv: TextView) {
    def txt2str: String = tv.getText.toString
  }

  implicit class CircularProgressBarHelper(v: CircularProgressBar) {
    private lazy val drawable = v.getIndeterminateDrawable.asInstanceOf[CircularProgressDrawable]
    def stop() = drawable.stop()
    def start() = drawable.start()
    def restart() = { drawable.stop(); drawable.start() }
  }

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

  implicit class AbsViewHolderHelper[T](adapter: AbsViewHolder[T]) {
    def findTextView(id: Int) = adapter.findViewByIdEfficient[TextView](id)
    def findTextView(parent: Int, id: Int) = adapter.findViewByIdEfficient[TextView](parent, id)
    def findImageView(id: Int) = adapter.findViewByIdEfficient[ImageView](id)
    def findImageView(parent: Int, id: Int) = adapter.findViewByIdEfficient[ImageView](parent, id)
  }

  implicit class MaterialDialogHelper(builder: MaterialDialog.Builder) {
    private var positiveListener: Option[MaterialDialog => Unit] = None
    private var negativeListener: Option[MaterialDialog => Unit] = None
    private var neutralListener: Option[MaterialDialog => Unit] = None

    builder.callback(new ButtonCallback {
      override def onPositive(dialog: MaterialDialog): Unit = positiveListener.map(l => l(dialog))
      override def onNegative(dialog: MaterialDialog): Unit = negativeListener.map(l => l(dialog))
      override def onNeutral(dialog: MaterialDialog): Unit = negativeListener.map(l => l(dialog))
    })

    def onPositive(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      positiveListener = Some(f)
      builder
    }

    def onNegative(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      negativeListener = Some(f)
      builder
    }

    def onNeutral(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      neutralListener = Some(f)
      builder
    }
  }
}
