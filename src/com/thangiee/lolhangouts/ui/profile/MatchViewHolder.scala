package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.text.Html
import android.view.View
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Match
import com.thangiee.lolhangouts.ui.utils._

class MatchViewHolder(v: View) extends AbsViewHolder[Match](v) {
  implicit private val ctx = getContext
  private val greaterOrEqToZero = (a: Double) => a >= 0
  private val lessOrEqToZero    = (a: Double) => a <= 0

  override def updateView(context: Context, m: Match): Unit = {
    this.findImageView(R.id.img_match_champ).setImageDrawable(ChampIconAsset(m.champName).toDrawable)
    this.findTextView(R.id.tv_champ_name).text(m.champName)
    this.findTextView(R.id.tv_match_type).text(m.queueType.replace("Ranked ", ""))
    this.findImageView(R.id.rect).setBackgroundColor(if (m.outCome.toLowerCase == "win")
      R.color.md_light_green_500.r2Color else R.color.md_red_500.r2Color)

    this.findTextView(R.id.tv_match_date).text(m.date)
    this.findTextView(R.id.tv_match_len).text(m.duration.split(":").head + "mins")

    this.findTextView(R.id.tv_match_perf).text = Html.fromHtml(makeColorText(m.overAllPerformance, greaterOrEqToZero, "%"))

    this.findTextView(R.id.tv_match_kills).text = Html.fromHtml(
      s"${m.avgKills.toInt} (${makeColorText(m.avgKillsPerformance, greaterOrEqToZero)})")

    this.findTextView(R.id.tv_match_deaths).text = Html.fromHtml(
      s"${m.avgDeaths.toInt} (${makeColorText(m.avgDeathsPerformance, lessOrEqToZero)})")

    this.findTextView(R.id.tv_match_assists).text = Html.fromHtml(
      s"${m.avgAssists.toInt} (${makeColorText(m.avgAssistsPerformance, greaterOrEqToZero)})")

    this.findTextView(R.id.tv_match_cs).text = Html.fromHtml(
      s"${m.avgCs} (${makeColorText(m.avgCsPerformance, greaterOrEqToZero)})")

    this.findTextView(R.id.tv_match_gold).text = Html.fromHtml(
      s"${(m.avgGold/1000.0).roundTo(1)}K (${makeColorText((m.avgGoldPerformance/1000.0).roundTo(1), greaterOrEqToZero, "k")})")
  }

  private def makeColorText(value: Double, op: (Double) => Boolean, append: String*) = {
    val green = "<font color='#8bc34a'>"
    val red = "<font color='#e51c23'>"
    s"${if (op(value)) green else red}$value" + append.mkString(" ") + "</font>"
  }
}
