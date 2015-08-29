package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.text.Html
import android.view.View
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Match
import com.thangiee.lolhangouts.ui.utils._
import com.github.nscala_time.time.Imports._

class MatchViewHolder(v: View) extends AbsViewHolder[Match](v) {
  implicit private val ctx = getContext

  private val green      = R.color.md_light_green_500.r2Color
  private val red        = R.color.md_red_500.r2Color
  private val timeFormat = DateTimeFormat.forPattern("MMM dd, yyyy")

  override def updateView(context: Context, m: Match): Unit = {
    this.findImageView(R.id.img_match_champ).setImageDrawable(ChampIconAsset(m.champName))
    this.findTextView(R.id.tv_champ_name).text = m.champName
    this.findTextView(R.id.tv_match_type).text = m.queueType.toLowerCase.capitalize.replace("_", " ")
    this.findImageView(R.id.rect).setBackgroundColor(if (m.isWin) green else red)
    this.findTextView(R.id.tv_match_cs).text = m.cs.toString
    this.findTextView(R.id.tv_match_gold).text = s"${(m.gold.toDouble / 1000).roundTo(1)}K"
    this.findTextView(R.id.tv_match_date).text = timeFormat.print(m.startTime)
    this.findTextView(R.id.tv_match_len).text = s"${m.duration / 60} mins"
    this.findTextView(R.id.tv_match_kda).text = Html.fromHtml(
      s"<font color='#8bc34a'>${m.kills}</font>/" +
      s"<font color='#e51c23'>${m.deaths}</font>/" +
      s"<font color='#fbc02d'>${m.assists}</font>"
    )

    List(
      (m.items1Id ,R.id.img_match_item1),
      (m.items2Id ,R.id.img_match_item2),
      (m.items3Id ,R.id.img_match_item3),
      (m.items4Id ,R.id.img_match_item4),
      (m.items5Id ,R.id.img_match_item5),
      (m.items6Id ,R.id.img_match_item6),
      (m.trinketId, R.id.img_match_trinket)
    ) foreach {
      case (itemId, imgId) => this.findImageView(imgId).setImageDrawable(ItemIconAsset(itemId))
    }
  }
}
