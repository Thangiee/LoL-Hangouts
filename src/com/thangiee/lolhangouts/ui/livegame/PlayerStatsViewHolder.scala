package com.thangiee.lolhangouts.ui.livegame

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import com.sakout.fancybuttons.FancyButton
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.domain.entities.PlayerStats
import com.thangiee.lolhangouts.ui.profile.ViewProfileActivity
import com.thangiee.lolhangouts.utils._

class PlayerStatsViewHolder(v: View) extends AbsViewHolder[PlayerStats](v) {
  implicit val ctx = getContext

  override def updateView(context: Context, p: PlayerStats): Unit = {
    // load champion icon
    this.findImageView(R.id.img_live_game_champ).setImageDrawable(ChampIconAssetFile(p.championName).toDrawable)
    // load spell 1 icon
    this.findImageView(R.id.img_live_game_spell1).setImageDrawable(SummonerSpellAssetFile(p.spellOne).toDrawable)
    // load spell 2 icon
    this.findImageView(R.id.img_live_game_spell2).setImageDrawable(SummonerSpellAssetFile(p.spellTwo).toDrawable)

    // load icon to indicate player is in a pre-made party
    p.partyNumber match {
      case Some(partyNum) =>
      this.findImageView(R.id.img_live_game_pre_made).setImageResource(if (p.teamNumber == 1) R.drawable.ic_action_users_light_blue else R.drawable.ic_action_users_light_purp)
      // show a number for the pre-made party to differentiate btw multiple pre-made party on the same team
      this.findTextView(R.id.tv_live_game_pre_made_num).text((partyNum + 1).toString)
        .textColor(if (p.teamNumber == 1) android.R.color.holo_blue_bright.r2Color else android.R.color.holo_purple.r2Color)
      case None =>
        this.findImageView(R.id.img_live_game_pre_made).setImageResource(android.R.color.transparent)
        this.findTextView(R.id.tv_live_game_pre_made_num).text(" ")
    }

    // load season 4 badge
    setBadgeDrawable(p.leagueTier, this.findImageView(R.id.img_s4_badge))

    // player's name with color base on which team
    this.findTextView(R.id.tv_live_game_name).text(p.playerName)
      .textColor(if (p.teamNumber == 1) android.R.color.holo_blue_dark.r2Color else android.R.color.holo_purple.r2Color)

    // populate other stats fields
    this.findTextView(R.id.tv_live_game_elo).text(p.elo.toString)
    this.findTextView(R.id.tv_live_game_s4_leag_info).text(p.leagueTier + " " + p.leagueDivision)
    this.findTextView(R.id.tv_live_game_s4_leag_lp).text(p.leaguePoints.toString + " LP")
    this.findTextView(R.id.tv_live_game_normal_w).text(p.normalWin + " W")
    this.findTextView(R.id.tv_live_game_rank_w).text(p.rankWins + " W")
    this.findTextView(R.id.tv_live_game_rank_l).text(p.rankLoses + " L")
    this.findTextView(R.id.tv_live_game_rank_k).text(p.killRatio.toString)
    this.findTextView(R.id.tv_live_game_rank_d).text(p.deathRatio.toString)
    this.findTextView(R.id.tv_live_game_rank_a).text(p.assistRatio.toString)

    findViewByIdEfficient[FancyButton](R.id.btn_live_game_profile).setTextColor(R.color.primary.r2Color)
    findViewByIdEfficient[FancyButton](R.id.btn_live_game_profile).setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = ctx.startActivity(ViewProfileActivity(p.playerName, p.regionId))
    })

    // setup the series images
    val seriesImageViews = List(R.id.img_live_game_serie_1, R.id.img_live_game_serie_2, R.id.img_live_game_serie_3,
      R.id.img_live_game_serie_4, R.id.img_live_game_serie_5)

    seriesImageViews.zipWithIndex.foreach { case (id, index) =>
      val iv = this.findImageView(id)
      p.series match {
        case Some(series) =>
          // has active series
          if (series.size == 3 && index < 3) setSeriesImgRes(iv, series(index))
          if (series.size == 5) setSeriesImgRes(iv, series(index))
        case None         =>
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

  private def setSeriesImgRes(imgView: ImageView, result: Char): Unit = {
    imgView.setVisibility(View.VISIBLE)
    result match {
      case 'W' => imgView.setImageResource(R.color.light_green)
      case 'L' => imgView.setImageResource(R.color.light_red) // lose result
      case 'N' => imgView.setImageResource(android.R.color.transparent) // no result yet
    }
  }


}