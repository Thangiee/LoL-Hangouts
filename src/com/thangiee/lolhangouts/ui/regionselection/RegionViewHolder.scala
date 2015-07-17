package com.thangiee.lolhangouts.ui.regionselection

import android.content.Context
import android.view.View.OnTouchListener
import android.view.{MotionEvent, View}
import android.widget.{ImageView, TextView}
import at.markushi.ui.RevealColorView
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolchat.region._
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.utils._

class RegionViewHolder(v: View) extends AbsViewHolder[Region](v) with OnTouchListener {
  implicit private val ctx = getContext
  v.setOnTouchListener(this)

  val gestureDetector = GestureDetectorBuilder()
    .onLongPress((e) => findViewByIdEfficient[RevealColorView](R.id.reveal).ripple(e.getX, e.getY, duration = 1000))
    .onSingleTapUp((e) => findViewByIdEfficient[RevealColorView](R.id.reveal).ripple(e.getX, e.getY))
    .build

  override def updateView(context: Context, region: Region): Unit = {
    findViewByIdEfficient[TextView](R.id.tv_region_name).setText(region.name)
    findViewByIdEfficient[ImageView](R.id.im_flag).setImageResource(getRegionFlag(region))
  }

  private def getRegionFlag(region: Region): Int = {
    region match {
      case NA   => R.drawable.ic_na
      case BR   => R.drawable.ic_br
      case EUNE => R.drawable.ic_eune
      case EUW  => R.drawable.ic_euw
      case KR   => R.drawable.ic_south_korea
      case LAN  => R.drawable.ic_latamn
      case LAS  => R.drawable.ic_latams
      case OCE  => R.drawable.ic_oce
      case RU   => R.drawable.ic_ru
      case TR   => R.drawable.ic_tr
    }
  }

  override def onTouch(view: View, event: MotionEvent): Boolean = {
    gestureDetector.onTouchEvent(event)
    false
  }
}
