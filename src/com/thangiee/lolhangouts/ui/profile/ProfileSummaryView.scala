package com.thangiee.lolhangouts.ui.profile

import java.text.DecimalFormat

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget._
import com.echo.holographlibrary.{PieGraph, PieSlice}
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.ProfileSummary
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCaseImpl
import com.thangiee.lolhangouts.ui.core.{CustomView, TActivity}
import com.thangiee.lolhangouts.ui.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.{ErrorView, RetryListener}

class ProfileSummaryView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val pieGraph     = find[PieGraph](R.id.pie_graph_win_rate)
  lazy val pieGroup     = find[RelativeLayout](R.id.pie_group)
  lazy val badgeGroup   = find[RelativeLayout](R.id.badge_group)
  lazy val statsGroup   = find[LinearLayout](R.id.stats_group)
  lazy val loadingWheel = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView    = find[ErrorView](R.id.error_view)
  lazy val header       = find[TextView](R.id.tv_profile_header)

  lazy val toolbar = ctx.asInstanceOf[TActivity].toolbar

  override val presenter = new ProfileSummaryPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_summary, this, false))
    pieGroup.setVisibility(View.INVISIBLE)
    badgeGroup.setVisibility(View.INVISIBLE)
    statsGroup.setVisibility(View.INVISIBLE)
    header.setVisibility(View.INVISIBLE)
  }

  override def onDetached(): Unit = {
    super.onDetached()
    toolbar.setSubtitle(null)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(new RetryListener {
      override def onRetry(): Unit = presenter.handleSetProfile(name, regionId)
    })
  }

  def initializeViewData(summary: ProfileSummary): Unit = {
    toolbar.setTitle(summary.summonerName)
    toolbar.setSubtitle(s"${summary.regionId.toUpperCase} - Level ${summary.level}")
    find[TextView](R.id.tv_profile_tier).setText(s"${summary.leagueTier} ${summary.leagueDivision}")
    find[TextView](R.id.tv_profile_league_name).setText(summary.leagueName)
    find[TextView](R.id.tv_profile_point).setText(s"${summary.leaguePoints} League Points")
    find[TextView](R.id.tv_profile_kda).setText(s"KDA: ${summary.kda} (${summary.killsRatio}/${summary.deathsRatio}/${summary.assistsRatio})")
    find[TextView](R.id.tv_profile_lost).setText(s"${summary.loses} L")
    find[TextView](R.id.tv_profile_win).setText(s"${summary.wins} W")
    find[TextView](R.id.tv_profile_games).setText(summary.games.toString)
    find[TextView](R.id.tv_profile_kills).setText(summary.kills.toString)
    find[TextView](R.id.tv_profile_deaths).setText(summary.deaths.toString)
    find[TextView](R.id.tv_profile_assists).setText(summary.assists.toString)
    find[TextView](R.id.tv_profile_elo).setText(summary.elo.toString)
    find[TextView](R.id.tv_profile_doubles).setText(summary.doubleKills.toString)
    find[TextView](R.id.tv_profile_triples).setText(summary.tripleKills.toString)
    find[TextView](R.id.tv_profile_quadra).setText(summary.quadraKills.toString)
    find[TextView](R.id.tv_profile_penta).setText(summary.pentaKills.toString)

    val rateTextView = find[TextView](R.id.tv_profile_rate)
    rateTextView.setText(new DecimalFormat("###.##").format(summary.winRate) + "%")
    rateTextView.setTextColor(if (summary.winRate >= 50) android.R.color.holo_green_dark.r2Color else R.color.red.r2Color)

    // set the badge image
    val badgeResId = summary.leagueTier.toUpperCase match {
      case "BRONZE"     => R.drawable.badge_bronze
      case "SILVER"     => R.drawable.badge_silver
      case "GOLD"       => R.drawable.badge_gold
      case "DIAMOND"    => R.drawable.badge_diamond
      case "PLATINUM"   => R.drawable.badge_platinum
      case "MASTER"     => R.drawable.badge_master
      case "CHALLENGER" => R.drawable.badge_challenger
      case _            => R.drawable.badge_unranked
    }
    find[ImageView](R.id.img_profile_badge).setImageResource(badgeResId)

    // setup the pie graph
    val winSlice = new PieSlice
    winSlice.setColor(android.R.color.holo_green_dark.r2Color)
    winSlice.setValue(5)
    winSlice.setGoalValue(summary.wins)
    pieGraph.addSlice(winSlice)

    val loseSlice = new PieSlice
    loseSlice.setColor(R.color.red.r2Color)
    loseSlice.setValue(5)
    loseSlice.setGoalValue(summary.loses)
    pieGraph.addSlice(loseSlice)

    pieGraph.setPadding(2)
    pieGraph.setInnerCircleRatio(R.integer.inner_circle_ratio.r2Integer)
    pieGraph.setInterpolator(new AccelerateDecelerateInterpolator())
    pieGraph.setDuration(1000)
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    loadingWheel.fadeInDown(duration = 1)
    pieGroup.setVisibility(View.INVISIBLE)
    badgeGroup.setVisibility(View.INVISIBLE)
    statsGroup.setVisibility(View.INVISIBLE)
    header.setVisibility(View.INVISIBLE)
    errorView.setVisibility(View.GONE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 1000) // delay in millis
    header.fadeIn(duration = 1, delay = 2000)
    badgeGroup.slideInLeft(delay = 1100)
    pieGroup.slideInRight(delay = 1350)
    statsGroup.slideInUp(delay = 1600)

    // don't animate pie chat (cause chat to not show) if user has not play any game
    if (find[TextView](R.id.tv_profile_games).txt2str.toInt != 0)
      delay(2600) { pieGraph.animateToGoalValues() }
  }

  def showDataNotFound(): Unit = showError("Oops", R.string.no_profile.r2String)

  def showGetDataError(): Unit = showError(
    title = (if (hasWifiConnection) R.string.server_busy else R.string.no_wifi).r2String,
    subTitle = R.string.err_get_data.r2String
  )

  private def showError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 1000) // delay in millis

    delay(2000) {
      errorView.setErrorTitle(title)
      errorView.setErrorSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}
