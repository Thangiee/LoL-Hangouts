package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget._
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCaseImpl
import com.thangiee.lolhangouts.data.usecases.entities.ProfileSummary
import com.thangiee.lolhangouts.ui.core.{CustomView, TActivity}
import com.thangiee.lolhangouts.ui.custom.ChampIconView
import com.thangiee.lolhangouts.ui.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import it.gmariotti.cardslib.library.view.CardViewNative
import lecho.lib.hellocharts.formatter.SimplePieChartValueFormatter
import lecho.lib.hellocharts.model.{PieChartData, SliceValue}
import lecho.lib.hellocharts.view.PieChartView
import tr.xip.errorview.ErrorView
import tr.xip.errorview.ErrorView.RetryListener

import scala.collection.JavaConversions._

class ProfileSummaryView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val winRatePieChart = find[PieChartView](R.id.pie_graph_win_rate)
  private lazy val kdaPieChart     = find[PieChartView](R.id.pie_graph_kda)
  private lazy val loadingWheel    = find[CircularProgressBar](R.id.circular_loader)
  private lazy val errorView       = find[ErrorView](R.id.error_view)
  private lazy val toolbar         = ctx.asInstanceOf[TActivity].toolbar

  private lazy val userCard       = find[CardViewNative](R.id.profile_summary_user_card)
  private lazy val statsCard      = find[CardViewNative](R.id.profile_summary_stats_card)
  private lazy val mostPlayedCard = find[CardViewNative](R.id.profile_summary_most_played_card)
  private lazy val rankedCard     = find[CardViewNative](R.id.profile_summary_ranked_card)

  override protected val presenter = new ProfileSummaryPresenter(this, ViewProfileUseCaseImpl())
  private var hasPlayGame = false

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_summary, this, false))
    Seq(userCard, statsCard, mostPlayedCard, rankedCard).foreach(_.setVisibility(View.INVISIBLE))
  }

  override def onDetached(): Unit = {
    super.onDetached()
    toolbar.setSubtitle(null)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(() => presenter.handleSetProfile(name, regionId))
  }

  def initializeViewData(ps: ProfileSummary): Unit = {
    if (ps.games > 0) hasPlayGame = true
    val frizeFont = FrizFontAsset().toTypeFace

    toolbar.setTitle(ps.summonerName)
    toolbar.setSubtitle(s"${ps.regionId.toUpperCase} - Level ${ps.level}")
    find[TextView](R.id.profile_summ_user_name).text(ps.summonerName.toUpperCase).typeface(frizeFont)
    find[TextView](R.id.profile_summ_user_reg_lvl).text(s"${ps.regionId.toUpperCase} - Level ${ps.level}")
    SummonerUtils.loadProfileIcon(ps.summonerName, ps.regionId, find[ImageView](R.id.profile_summ_user_icon), 64)
    find[TextView](R.id.tv_stats_title).typeface(frizeFont)

    find[TextView](R.id.tv_most_played_title).typeface(frizeFont)
    Seq(
      (0, R.id.profile_summ_champ_icon1, R.id.profile_summ_champ_kda1, R.id.profile_summ_champ_games1),
      (1, R.id.profile_summ_champ_icon2, R.id.profile_summ_champ_kda2, R.id.profile_summ_champ_games2),
      (2, R.id.profile_summ_champ_icon3, R.id.profile_summ_champ_kda3, R.id.profile_summ_champ_games3),
      (3, R.id.profile_summ_champ_icon4, R.id.profile_summ_champ_kda4, R.id.profile_summ_champ_games4)
    ).foreach {
      case (i, iconId, kdaId, gamesId) => ps.mostPlayedChamps.lift(i).foreach { champ =>
        find[ChampIconView](iconId).setChampion(champ.name)
        find[TextView](gamesId).text = s"Games:${champ.games}"
        find[TextView](kdaId).text = Html.fromHtml(s"<font color='#8bc34a'>${champ.killsRatio.roundTo(1)}</font>/" +
                                                   s"<font color='#e51c23'>${champ.deathsRatio.roundTo(1)}</font>/" +
                                                   s"<font color='#fbc02d'>${champ.assistsRatio.roundTo(1)}</font>")
      }
    }

    find[TextView](R.id.tv_ranked_title).typeface(frizeFont)
    find[TextView](R.id.tv_solo_duo_title).typeface(frizeFont)
    find[TextView](R.id.profile_summ_rank_league).text(s"${ps.leagueTier} ${ps.leagueDivision}").typeface(frizeFont)
    find[TextView](R.id.profile_summ_rank_lp).text(s"${ps.leaguePoints} LP").typeface(frizeFont)
    find[TextView](R.id.profile_summ_rank_wins).text(s"${ps.wins} Wins").typeface(frizeFont)
    find[TextView](R.id.profile_summ_rank_elo).text(ps.elo + " ELO").typeface(frizeFont)

    // set the badge image
    val badgeResId = ps.leagueTier.toUpperCase match {
      case "BRONZE"     => R.drawable.badge_bronze
      case "SILVER"     => R.drawable.badge_silver
      case "GOLD"       => R.drawable.badge_gold
      case "DIAMOND"    => R.drawable.badge_diamond
      case "PLATINUM"   => R.drawable.badge_platinum
      case "MASTER"     => R.drawable.badge_master
      case "CHALLENGER" => R.drawable.badge_challenger
      case _            => R.drawable.badge_unranked
    }
    find[ImageView](R.id.profile_summ_rank_badge).setImageResource(badgeResId)

    // setup the win rate pie chart
    // When a value is 0, it causes the graph to disappear so set it to 1 as a work around.
    val winSlice = new SliceValue(1, R.color.md_light_green_500.r2Color)
    winSlice.setTarget(if (ps.wins == 0) 1 else ps.wins)
    val loseSlice = new SliceValue(1, R.color.md_red_500.r2Color)
    loseSlice.setTarget(if (ps.loses == 0) 1 else ps.loses)

    val data = new PieChartData(Seq(winSlice, loseSlice))
    data.setHasLabels(true)
    data.setHasLabelsOutside(false)
    data.setHasCenterCircle(true)
    data.setHasLabelsOnlyForSelected(false)
    data.setCenterText1(ps.winRate.roundTo(1) + "%")
    data.setCenterText1Typeface(frizeFont)
    data.setCenterText1FontSize(16)
    data.setCenterText1Color(if (ps.winRate >= 50) R.color.md_light_green_500.r2Color else R.color.md_red_500.r2Color)
    data.setCenterText2(R.string.win_rate.r2String)
    data.setCenterText2FontSize(12)
    data.setValueLabelTextSize(10)

    winRatePieChart.setInteractive(true)
    winRatePieChart.setPieChartData(data)
    winRatePieChart.setValueSelectionEnabled(false)

    // Setup the kda pie chart.
    // When a value is 0, it causes the graph to disappear so set it to 1 as a work around.
    val killSlice = new SliceValue(1.0f, R.color.md_light_green_500.r2Color)
    killSlice.setTarget(if (ps.killsRatio == 0) 1 else ps.killsRatio.roundTo(1).toFloat)
    val deathSlice = new SliceValue(1.0f, R.color.md_red_500.r2Color)
    deathSlice.setTarget(if (ps.deathsRatio == 0) 1 else ps.deathsRatio.roundTo(1).toFloat)
    val assistSlice = new SliceValue(1.0f, R.color.md_yellow_500.r2Color)
    assistSlice.setTarget(if (ps.assistsRatio == 0) 1 else ps.assistsRatio.roundTo(1).toFloat)

    val data2 = new PieChartData(Seq(killSlice, assistSlice, deathSlice))
    data2.setHasLabels(true)
    data2.setHasLabelsOutside(false)
    data2.setHasCenterCircle(true)
    data2.setHasLabelsOnlyForSelected(false)
    data2.setCenterText1(ps.kda.roundTo(1).toString)
    data2.setCenterText1Typeface(frizeFont)
    data2.setCenterText1FontSize(16)
    data2.setCenterText2(R.string.kda.r2String)
    data2.setCenterText2FontSize(12)
    data2.setValueLabelTextSize(10)
    data2.setFormatter(new SimplePieChartValueFormatter(1).setDecimalSeparator('.'))

    kdaPieChart.setInteractive(true)
    kdaPieChart.setPieChartData(data2)
    kdaPieChart.setValueSelectionEnabled(false)
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    loadingWheel.fadeInDown(duration = 1)
    Seq(userCard, statsCard, mostPlayedCard, rankedCard).foreach(_.setVisibility(View.INVISIBLE))
    errorView.setVisibility(View.GONE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 1000) // delay in millis
    userCard.fadeIn(delay = 1100)
    statsCard.fadeIn(delay = 1350)
    mostPlayedCard.fadeIn(delay = 1600)
    rankedCard.fadeIn(delay = 1850)

    // don't animate pie chat (cause chat to not show) if user has not play any game
    delay(2900) {
      if (hasPlayGame) {
        winRatePieChart.startDataAnimation(1500)
        kdaPieChart.startDataAnimation(1500)
      }
    }
  }

  def showDataNotFound(): Unit = showError(R.string.oops.r2String, R.string.no_profile.r2String)

  def showGetDataError(): Unit = showError(
    title = (if (hasWifiConnection) R.string.server_busy else R.string.no_wifi).r2String,
    subTitle = R.string.err_get_data.r2String
  )

  def showAppNeedUpdate(): Unit = showError(R.string.app_out_of_date_title.r2String, R.string.app_out_of_date_body.r2String)

  private def showError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 1000) // delay in millis

    delay(2000) {
      errorView.setTitle(title)
      errorView.setSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}
