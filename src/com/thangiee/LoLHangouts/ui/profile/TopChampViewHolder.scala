package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.graphics.Color
import android.support.v7.graphics.Palette
import android.support.v7.graphics.Palette.PaletteAsyncListener
import android.view.View
import android.widget.ImageView
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.domain.entities.TopChampion
import com.thangiee.LoLHangouts.utils._

class TopChampViewHolder(v: View) extends AbsViewHolder[TopChampion](v) {
  implicit val ctx = getContext
  val green = R.color.md_light_green_600.r2Color
  val red = R.color.red.r2Color

  val paletteAsyncListener = new PaletteAsyncListener {
    override def onGenerated(palette: Palette): Unit = {
      val color = palette.getVibrantColor(R.color.primary.r2Color)

      // make color brighter where factor controls the brightness: 0 - unchanged, 1 - white
      val factor = .5
      val red = ((Color.red(color) * (1 - factor) / 255 + factor) * 255).toInt
      val green = ((Color.green(color) * (1 - factor) / 255 + factor) * 255).toInt
      val blue = ((Color.blue(color) * (1 - factor) / 255 + factor) * 255).toInt
      val brighterColor = Color.argb(Color.alpha(color), red, green, blue)

      findViewByIdEfficient[ImageView](R.id.rect).setBackgroundColor(brighterColor)
    }
  }

  override def updateView(context: Context, c: TopChampion): Unit = {
    this.findImageView(R.id.img_champ_icon).setImageDrawable(ChampIconAssetFile(c.name).toDrawable)
    this.findTextView(R.id.tv_champ_name).text(c.name)

    this.findTextView(R.id.tv_champ_perf).text(c.overAllPerformance + "%")
      .textColor(if (c.overAllPerformance >= 0) green else red)

    this.findTextView(R.id.tv_champ_game).text(c.numOfGames.toString)

    this.findTextView(R.id.tv_champ_win_rate).text(c.winsRate + "%")
      .textColor(if (c.winsRate >= 0) green else red)

    this.findTextView(R.id.tv_champ_avg_k).text(c.avgKills.toString)
    this.findTextView(R.id.tv_champ_avg_more_k).text(c.avgKillsPerformance.toString)
      .textColor(if (c.avgKillsPerformance >= 0) green else red)

    this.findTextView(R.id.tv_champ_avg_d).text(c.avgDeaths.toString)
    this.findTextView(R.id.tv_champ_avg_more_d).text(c.avgDeathsPerformance.toString)
      .textColor(if (c.avgDeathsPerformance <= 0) green else red)

    this.findTextView(R.id.tv_champ_avg_a).text(c.avgAssists.toString)
    this.findTextView(R.id.tv_champ_avg_more_a).text(c.avgAssistsPerformance.toString)
      .textColor(if (c.avgAssistsPerformance >= 0) green else red)

    this.findTextView(R.id.tv_champ_avg_cs).text(c.avgCs.toString)
    this.findTextView(R.id.tv_champ_avg_more_cs).text(c.avgCsPerformance.toString)
      .textColor(if (c.avgCsPerformance >= 0) green else red)

    this.findTextView(R.id.tv_champ_avg_g).text(c.avgGold.toString)
    this.findTextView(R.id.tv_champ_avg_more_g).text(c.avgGoldPerformance.toString)
      .textColor(if (c.avgGoldPerformance >= 0) green else red)

    Palette.generateAsync(ChampIconAssetFile(c.name).toBitmap, paletteAsyncListener)
  }
}
