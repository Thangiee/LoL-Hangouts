package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.graphics.Color
import android.support.v7.graphics.Palette
import android.support.v7.graphics.Palette.PaletteAsyncListener
import android.view.View
import android.widget.ImageView
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Match
import com.thangiee.lolhangouts.ui.utils._

class MatchViewHolder(v: View) extends AbsViewHolder[Match](v) {
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

  override def updateView(context: Context, m: Match): Unit = {
    this.findImageView(R.id.img_match_champ).setImageDrawable(ChampIconAssetFile(m.champName).toDrawable)
    this.findTextView(R.id.tv_match_type).text(m.queueType)
    this.findTextView(R.id.tv_match_outcome).text(m.outCome)
      .textColor(if (m.outCome.toLowerCase == "win") green else red)

    this.findTextView(R.id.tv_match_date).text(m.date)
    this.findTextView(R.id.tv_match_len).text(m.duration)

    this.findTextView(R.id.tv_match_perf).text(m.overAllPerformance + "%")
      .textColor(if (m.overAllPerformance >= 0) green else red)

    this.findTextView(R.id.tv_match_avg_k).text(m.avgKills.toString)
    this.findTextView(R.id.tv_match_avg_more_k).text(m.avgKillsPerformance.toString)
      .textColor(if (m.avgKillsPerformance >= 0) green else red)

    this.findTextView(R.id.tv_match_avg_d).text(m.avgDeaths.toString)
    this.findTextView(R.id.tv_match_avg_more_d).text(m.avgDeathsPerformance.toString)
      .textColor(if (m.avgDeathsPerformance <= 0) green else red)

    this.findTextView(R.id.tv_match_avg_a).text(m.avgAssists.toString)
    this.findTextView(R.id.tv_match_avg_more_a).text(m.avgAssistsPerformance.toString)
      .textColor(if (m.avgAssistsPerformance >= 0) green else red)

    this.findTextView(R.id.tv_match_avg_cs).text(m.avgCs.toString)
    this.findTextView(R.id.tv_match_avg_more_cs).text(m.avgCsPerformance.toString)
      .textColor(if (m.avgCsPerformance >= 0) green else red)

    Palette.generateAsync(ChampIconAssetFile(m.champName).toBitmap, paletteAsyncListener)
  }
}
