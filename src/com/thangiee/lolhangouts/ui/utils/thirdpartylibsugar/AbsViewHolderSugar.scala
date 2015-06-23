package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.widget.{ImageView, TextView}
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder

trait AbsViewHolderSugar {
  implicit class AbsViewHolderSugar[T](adapter: AbsViewHolder[T]) {
    def findTextView(id: Int) = adapter.findViewByIdEfficient[TextView](id)
    def findTextView(parent: Int, id: Int) = adapter.findViewByIdEfficient[TextView](parent, id)
    def findImageView(id: Int) = adapter.findViewByIdEfficient[ImageView](id)
    def findImageView(parent: Int, id: Int) = adapter.findViewByIdEfficient[ImageView](parent, id)
  }
}

object AbsViewHolderSugar extends AbsViewHolderSugar
