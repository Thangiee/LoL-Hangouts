package com.thangiee.lolhangouts.ui.livegame

import android.content.Context
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.PlayerStats
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.livegame.GameScouterTeamView.BlueTeam
import com.thangiee.lolhangouts.ui.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.ErrorView

import scala.collection.JavaConversions._

class GameScouterTeamView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val playersRecView = find[RecyclerView](R.id.recycler_view)
  private lazy val loadingWheel   = find[CircularProgressBar](R.id.circular_loader)
  private lazy val errorView      = find[ErrorView](R.id.error_view)

  override protected val presenter = new GameScouterTeamPresenter(this)

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

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    playersRecView.setVisibility(View.INVISIBLE)
    errorView.setVisibility(View.GONE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut()
    playersRecView.fadeIn(duration = 1, delay = 1000)
  }

  def showLoadingError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut()
    errorView.showRetryButton(false)

    delay(1000) {
      errorView.setTitle(title)
      errorView.setSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }

  def initializeViewData(team: List[PlayerStats], teamColor: Int): Unit = {
    playersRecView.setBackgroundColor((if (teamColor == BlueTeam) R.color.md_light_blue_50 else R.color.md_deep_purple_50).r2Color)
    val adapter = new SimpleAdapter[PlayerStats](R.layout.line_item_game_scouter_player, classOf[PlayerStatsViewHolder], team)
    playersRecView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[PlayerStatsViewHolder]])
  }
}

object GameScouterTeamView {
  val BlueTeam   = 1
  val PurpleTeam = 2
}
