package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Match
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCaseImpl
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.regionselection.RegionViewHolder
import com.thangiee.lolhangouts.ui.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter
import tr.xip.errorview.{ErrorView, RetryListener}

import scala.collection.JavaConversions._

class ProfileMatchHistView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val loadingWheel      = find[CircularProgressBar](R.id.circular_loader)
  private lazy val errorView         = find[ErrorView](R.id.error_view)
  private lazy val matchRecyclerView = find[RecyclerView](R.id.recycler_view)

  override protected val presenter = new ProfileMatchHistPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_match_hist, this, false))
    val llm = new LinearLayoutManager(ctx)
    llm.setSmoothScrollbarEnabled(true)
    matchRecyclerView.setLayoutManager(llm)
    matchRecyclerView.setHasFixedSize(true)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(new RetryListener {
      override def onRetry(): Unit = presenter.handleSetProfile(name, regionId)
    })
  }

  def initializeViewData(matches: List[Match]): Unit = {
    delay(500) { // wait for loading wheel to hide
      val adapter = new SimpleAdapter[Match](R.layout.card_match_hist, classOf[MatchViewHolder], matches).asInstanceOf[RecyclerView.Adapter[RegionViewHolder]]
      val alphaInAdapter = new AlphaInAnimationAdapter(adapter)
      alphaInAdapter.setDuration(500)
      matchRecyclerView.setAdapter(alphaInAdapter)
    }
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    errorView.setVisibility(View.GONE)
    loadingWheel.fadeInDown(duration = 1)
    matchRecyclerView.setVisibility(View.INVISIBLE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 500)
    matchRecyclerView.fadeIn(duration = 1, delay = 500)
  }

  def showDataNotFound(): Unit = showError("No Result", R.string.no_match_hist.r2String)

  def showGetDataError(): Unit = showError(
    title = (if (hasWifiConnection) R.string.server_busy else R.string.no_wifi).r2String,
    subTitle = R.string.err_get_data.r2String
  )

  private def showError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 500) // delay in millis

    delay(1500) {
      errorView.setErrorTitle(title)
      errorView.setErrorSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}
