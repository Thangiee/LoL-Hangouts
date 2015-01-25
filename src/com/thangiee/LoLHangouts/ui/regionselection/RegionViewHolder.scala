package com.thangiee.LoLHangouts.ui.regionselection

import android.content.Context
import android.view.View
import android.widget.{ImageView, TextView}
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.domain.entities._

class RegionViewHolder(v: View) extends AbsViewHolder[Region](v) {

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

}
