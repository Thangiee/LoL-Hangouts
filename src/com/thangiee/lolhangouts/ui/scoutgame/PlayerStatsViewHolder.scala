package com.thangiee.lolhangouts.ui.scoutgame

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import com.makeramen.roundedimageview.RoundedImageView
import mehdi.sakout.fancybuttons.FancyButton
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.PlayerStats
import com.thangiee.lolhangouts.ui.profile.ViewProfileActivity
import com.thangiee.lolhangouts.ui.utils._

class PlayerStatsViewHolder(v: View) extends AbsViewHolder[PlayerStats](v) {
  implicit private val ctx = getContext

  override def updateView(context: Context, p: PlayerStats): Unit = {

    // load champion icon
    this.findImageView(R.id.img_live_game_champ).setImageDrawable(ChampIconAsset(p.championName))
    // load spell 1 icon
    this.findImageView(R.id.img_live_game_spell1).setImageDrawable(SummonerSpellAsset(p.spellOne))
    // load spell 2 icon
    this.findImageView(R.id.img_live_game_spell2).setImageDrawable(SummonerSpellAsset(p.spellTwo))

    // load season 4 badge
    setBadgeDrawable(p.leagueTier, this.findImageView(R.id.img_s4_badge))

    // player's name with color base on which team
    this.findTextView(R.id.tv_live_game_name).text(p.playerName)
      .textColor(if (p.teamNumber == 100) android.R.color.holo_blue_dark.r2Color else android.R.color.holo_purple.r2Color)

    // populate other stats fields
    this.findTextView(R.id.tv_live_game_elo).text(p.elo.toString)
    this.findTextView(R.id.tv_live_game_s4_leag_info).text(p.leagueTier + " " + p.leagueDivision)
    this.findTextView(R.id.tv_live_game_s4_leag_lp).text(p.leaguePoints.toString + " LP")
    this.findTextView(R.id.tv_live_game_normal_w).text(p.normalWin + " W")
    this.findTextView(R.id.tv_live_game_rank_w).text(p.rankWins + " W")
    this.findTextView(R.id.tv_live_game_rank_l).text(p.rankLoses + " L")
    this.findTextView(R.id.tv_live_game_rank_k).text(p.killRatio.roundTo(1).toString)
    this.findTextView(R.id.tv_live_game_rank_d).text(p.deathRatio.roundTo(1).toString)
    this.findTextView(R.id.tv_live_game_rank_a).text(p.assistRatio.roundTo(1).toString)

    findViewByIdEfficient[FancyButton](R.id.btn_live_game_profile).setTextColor(R.color.primary.r2Color)
    findViewByIdEfficient[FancyButton](R.id.btn_live_game_profile).setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = ctx.startActivity(ViewProfileActivity(p.playerName, p.regionId))
    })

    // setup the series images
    val seriesImageViews = List(R.id.img_live_game_serie_1, R.id.img_live_game_serie_2, R.id.img_live_game_serie_3,
      R.id.img_live_game_serie_4, R.id.img_live_game_serie_5)

    seriesImageViews.zipWithIndex.foreach { case (id, index) =>
      val iv = this.findViewByIdEfficient[RoundedImageView](id)

      if (p.series.nonEmpty) {
        // has active series
        if (p.series.length == 3 && index < 3) setSeriesImgRes(iv, p.series(index))
        if (p.series.length == 5) setSeriesImgRes(iv, p.series(index))
      } else {
        // no active series
        iv.setVisibility(View.INVISIBLE)
      }
    }
  }

  private def setBadgeDrawable(tier: String, img: ImageView): Unit = {
    tier.toUpperCase match {
      case "BRONZE"     ⇒ img.setImageResource(R.drawable.badge_bronze)
      case "SILVER"     ⇒ img.setImageResource(R.drawable.badge_silver)
      case "GOLD"       ⇒ img.setImageResource(R.drawable.badge_gold)
      case "DIAMOND"    ⇒ img.setImageResource(R.drawable.badge_diamond)
      case "PLATINUM"   ⇒ img.setImageResource(R.drawable.badge_platinum)
      case "MASTER"     ⇒ img.setImageResource(R.drawable.badge_master)
      case "CHALLENGER" ⇒ img.setImageResource(R.drawable.badge_challenger)
      case _            ⇒ img.setImageResource(R.drawable.badge_unranked)
    }
  }

  private def setSeriesImgRes(imgView: RoundedImageView, result: String): Unit = {
    imgView.setVisibility(View.VISIBLE)
    result match {
      case "W" => imgView.setImageResource(R.drawable.circle_green)
      case "L" => imgView.setImageResource(R.drawable.circle_red) // lose result
      case "N" => imgView.setImageResource(R.drawable.circle_gray) // no result yet
    }
  }


}
