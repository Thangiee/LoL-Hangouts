package com.thangiee.LoLHangouts.ui.livegame

import android.content.Context
import android.os.SystemClock
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.LoLHangouts.domain.entities.PlayerStats
import com.thangiee.LoLHangouts.ui.core.CustomView
import com.thangiee.LoLHangouts.ui.livegame.LiveGameTeamView.BlueTeam
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.R
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.ErrorView

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LiveGameTeamView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val playersRecView = find[RecyclerView](R.id.recycler_view)
  lazy val loadingWheel   = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView      = find[ErrorView](R.id.error_view)

  override val presenter = new LiveGameTeamPresenter(this)

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.live_game_team_view, this, false))
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
    playersRecView.setBackgroundColor((if (teamColor == BlueTeam) R.color.md_light_blue_50 else R.color.md_deep_purple_50).r2Color)
    val adapter = new SimpleAdapter[PlayerStats](R.layout.line_item_live_game_player, classOf[PlayerStatsViewHolder], team)
    playersRecView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[PlayerStatsViewHolder]])
  }
}

object LiveGameTeamView {
  val BlueTeam   = 1
  val PurpleTeam = 2
}
