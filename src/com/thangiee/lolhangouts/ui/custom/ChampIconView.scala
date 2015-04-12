package com.thangiee.lolhangouts.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.{TextView, ImageView, FrameLayout}
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.core.{Presenter, CustomView}
import com.thangiee.lolhangouts.ui.utils._

class ChampIconView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val champIcon = find[ImageView](R.id.img_champ_icon)
  private lazy val champName = find[TextView](R.id.tv_champ_name)

  override val presenter: Presenter = new Presenter {}

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.champion_icon_view, this, false))
  }

  def setChampion(name: String) = {
    champIcon.setImageDrawable(ChampIconAsset(name).toDrawable)
    champName.text = name
  }
}
