package com.thangiee.lolhangouts.ui.scoutgame

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pnikosis.materialishprogress.ProgressWheel
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCaseImpl
import com.thangiee.lolhangouts.data.usecases.entities.PlayerStats
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.scoutgame.ScoutGameView.BlueTeam
import com.thangiee.lolhangouts.ui.utils._
import tr.xip.errorview.ErrorView

import scala.collection.JavaConversions._

class ScoutGameView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val playersRecView = find[RecyclerView](R.id.rv_suggestions)
  private lazy val loadingWheel   = find[ProgressWheel](R.id.loading_wheel)
  private lazy val errorView      = find[ErrorView](R.id.error_view)

  override protected val presenter = new ScoutGamePresenter(this, ScoutGameUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.game_scouter_team_view, this, false))
    val llm = new LinearLayoutManager(ctx)
    llm.setSmoothScrollbarEnabled(true)
    playersRecView.setLayoutManager(llm)
    playersRecView.setHasFixedSize(true)
    playersRecView.setVerticalScrollBarEnabled(true)
    playersRecView.setItemViewCacheSize(5)
    showLoading()
  }

  def scoutGame(username: String, regionId: String, teamColor: Int): Unit = {
    presenter.handleScoutingGame(username, regionId, teamColor)
  }

  def initializeViewData(mapName: String, regionId: String, teamColor: Int, team: List[PlayerStats]): Unit = {
    setToolbarTitle(s"$mapName - $regionId")
    playersRecView.setBackgroundColor((if (teamColor == BlueTeam) R.color.md_light_blue_50 else R.color.md_deep_purple_50).r2Color)
    val adapter = new SimpleAdapter[PlayerStats](R.layout.line_item_game_scouter_player, classOf[PlayerStatsViewHolder], team)
    playersRecView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[PlayerStatsViewHolder]])
  }

  def showLoading(): Unit = {
    loadingWheel.spin()
    loadingWheel.setVisibility(View.VISIBLE)
    playersRecView.setVisibility(View.INVISIBLE)
    errorView.setVisibility(View.GONE)
  }

  def hideLoading(): Unit = {
    loadingWheel.setProgress(1)
    loadingWheel.fadeOutUp(duration = 750, delay = 1000)
    playersRecView.fadeIn(duration = 1, delay = 1750)
  }

  def showDataNotFound(username: String, snackBarAction: => Unit): Unit =
    showError("Oops", username + R.string.not_in_game.r2String, snackBarAction)

  def showGetDataError(snackBarAction: => Unit): Unit =
    showError("Oops", R.string.err_get_data.r2String, snackBarAction)

  def showAppNeedUpdate(snackBarAction: => Unit): Unit =
    showError(R.string.app_out_of_date_title.r2String, R.string.app_out_of_date_body.r2String, snackBarAction)

  private def showError(title: String, subTitle: String, f: => Unit): Unit = {
    SnackBar("Failed to load game")
      .setDuration(Snackbar.LENGTH_INDEFINITE)
      .setAction("Reload", f)
      .show()

    loadingWheel.fadeOutUp(delay = 1000)
    errorView.showRetryButton(false)

    delay(mills = 1000) {
      errorView.setTitle(title)
      errorView.setSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}

object ScoutGameView {
  val BlueTeam   = 1
  val PurpleTeam = 2
}
