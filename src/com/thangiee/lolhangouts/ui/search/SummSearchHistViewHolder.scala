package com.thangiee.lolhangouts.ui.search

import android.content.Context
import android.text.Html
import android.view.View.OnTouchListener
import android.view.{MotionEvent, View}
import android.widget.{ImageView, TextView}
import at.markushi.ui.RevealColorView
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.utils._
import com.thangiee.lolhangouts.data.usecases.entities.SummSearchHist

class SummSearchHistViewHolder(v: View) extends AbsViewHolder[SummSearchHist](v) with OnTouchListener {
  implicit private val ctx = getContext
  v.setOnTouchListener(this)

  val gestureDetector = GestureDetectorBuilder()
    .onLongPress((e) => findViewByIdEfficient[RevealColorView](R.id.reveal).ripple(e.getX, e.getY, duration = 1000))
    .onSingleTapUp((e) => findViewByIdEfficient[RevealColorView](R.id.reveal).ripple(e.getX, e.getY))
    .build

  def updateView(context: Context, result: SummSearchHist): Unit = {
    findViewByIdEfficient[TextView](R.id.tv_summ_name)
      .setText(Html.fromHtml(s"${result.name} <i>(${result.regionId.toUpperCase})</i>"))

    findViewByIdEfficient[ImageView](R.id.img_suggestion_icon)
      .setImageResource(if (result.isFriend) R.drawable.ic_smily else R.drawable.ic_action_history)
  }

  override def onTouch(view: View, event: MotionEvent): Boolean = {
    gestureDetector.onTouchEvent(event)
    false
  }
}
