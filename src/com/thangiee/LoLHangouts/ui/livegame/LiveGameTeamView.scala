package com.thangiee.LoLHangouts.ui.livegame

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.widget.{FrameLayout, ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.activities.ViewProfileActivity
import com.thangiee.LoLHangouts.domain.entities.PlayerStats
import com.thangiee.LoLHangouts.ui.livegame.LiveGameTeamView._
import com.thangiee.LoLHangouts.utils.{ChampIconAssetFile, SummonerSpellAssetFile, _}
import com.thangiee.LoLHangouts.{CustomView, R}
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.ErrorView

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LiveGameTeamView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val playerListView = find[ListView](R.id.listView)
  lazy val loadingWheel   = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView      = find[ErrorView](R.id.error_view)

  override val presenter = new LiveGameTeamPresenter(this)

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.live_game_team_view, this, false))
    showLoading()
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    playerListView.setVisibility(View.INVISIBLE)
    errorView.setVisibility(View.GONE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut()
    playerListView.fadeIn(duration = 1, delay = 1000)
  }

  def showLoadingError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut()
    errorView.showRetryButton(false)

    Future {
      SystemClock.sleep(1000)
      runOnUiThread {
        errorView.setErrorTitle(title)
        errorView.setErrorSubtitle(subTitle)
        errorView.setVisibility(View.VISIBLE)
      }
    }
  }

  def initializeViewData(team: List[PlayerStats], teamColor: Int): Unit = {
    val playerDict = new BindDictionary[PlayerStats]()
    val preMadeParties = team.filter(p ⇒ p.partyId.isDefined).map(p ⇒ p.partyId.get).distinct.zipWithIndex.toMap

    // load champion icon
    playerDict.addStaticImageField(R.id.img_live_game_champ, new StaticImageLoader[PlayerStats] {
      override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit =
        iv.setImageDrawable(ChampIconAssetFile(p.championName).toDrawable)
    })

    // load spell 1 icon
    playerDict.addStaticImageField(R.id.img_live_game_spell1, new StaticImageLoader[PlayerStats] {
      override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit =
        iv.setImageDrawable(SummonerSpellAssetFile(p.spellOne).toDrawable)
    })

    // load spell 2 icon
    playerDict.addStaticImageField(R.id.img_live_game_spell2, new StaticImageLoader[PlayerStats] {
      override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit =
        iv.setImageDrawable(SummonerSpellAssetFile(p.spellTwo).toDrawable)
    })

    // load icon to indicate player is in a pre-made party
    playerDict.addStaticImageField(R.id.img_live_game_pre_made, new StaticImageLoader[PlayerStats] {
      override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit =
        preMadeParties.get(p.partyId.getOrElse(-1)) match {
          case Some(partyIndex) =>
            if (teamColor == BlueTeam)   iv.setImageResource(R.drawable.ic_action_users_light_blue)
            if (teamColor == PurpleTeam) iv.setImageResource(R.drawable.ic_action_users_light_purp)
          case None             => iv.setImageResource(android.R.color.transparent)
        }
    })

    // show a number for the pre-made party to differentiate btw multiple pre-made party on the same team
    playerDict.addStringField(R.id.tv_live_game_pre_made_num, (p: PlayerStats) =>
      preMadeParties.get(p.partyId.getOrElse(-1)).map(partyIndex => s"${partyIndex + 1}").getOrElse(" "))
      .conditionalTextColor((p: PlayerStats) => teamColor == BlueTeam, android.R.color.holo_blue_bright.r2Color, android.R.color.holo_purple.r2Color)

    // load season 4 badge
    playerDict.addStaticImageField(R.id.img_s4_badge, new StaticImageLoader[PlayerStats] {
      override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit = setBadgeDrawable(p.leagueTier, iv)
    })

    // player's name with color base on which team
    playerDict.addStringField(R.id.tv_live_game_name, (p: PlayerStats) => p.playerName)
      .conditionalTextColor((p: PlayerStats) => teamColor == BlueTeam, android.R.color.holo_blue_dark.r2Color, android.R.color.holo_purple.r2Color)

    // populate other stats fields
    playerDict.addStringField(R.id.tv_live_game_elo, (p: PlayerStats) => p.elo.toString)
    playerDict.addStringField(R.id.tv_live_game_s4_leag_info, (p: PlayerStats) => p.leagueTier + " " + p.leagueDivision)
    playerDict.addStringField(R.id.tv_live_game_s4_leag_lp, (p: PlayerStats) => p.leaguePoints.toString + " LP")
    playerDict.addStringField(R.id.tv_live_game_normal_w, (p: PlayerStats) => p.normalWin + " W")
    playerDict.addStringField(R.id.tv_live_game_rank_w, (p: PlayerStats) => p.rankWins + " W")
    playerDict.addStringField(R.id.tv_live_game_rank_l, (p: PlayerStats) => p.rankLoses + " L")
    playerDict.addStringField(R.id.tv_live_game_rank_k, (p: PlayerStats) => p.killRatio.toString)
    playerDict.addStringField(R.id.tv_live_game_rank_d, (p: PlayerStats) => p.deathRatio.toString)
    playerDict.addStringField(R.id.tv_live_game_rank_a, (p: PlayerStats) => p.assistRatio.toString)

    // setup button to view player profile
    playerDict.addBaseField(R.id.btn_live_game_profile)
      .onClick((p: PlayerStats) => ctx.startActivity(ViewProfileActivity(p.playerName, p.regionId)))

    // setup the series images
    List(R.id.img_live_game_serie_1, R.id.img_live_game_serie_2, R.id.img_live_game_serie_3,
      R.id.img_live_game_serie_4, R.id.img_live_game_serie_5).zipWithIndex.foreach { case (id, index) =>
      playerDict.addStaticImageField(id, new StaticImageLoader[PlayerStats] {
        override def loadImage(p: PlayerStats, iv: ImageView, p3: Int): Unit =
          p.series.map { series =>
            // has active series
            if (series.size == 3 && index < 3) setSeriesImgRes(iv, series(index))
            if (series.size == 5) setSeriesImgRes(iv, series(index))
          }.getOrElse(iv.setVisibility(View.INVISIBLE)) // no active series
      })
    }

    val adapter = new FunDapter[PlayerStats](ctx, team, R.layout.live_game_player_view, playerDict)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    playerListView.setAdapter(adapter)
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

object LiveGameTeamView {
  val BlueTeam   = 1
  val PurpleTeam = 2
}
