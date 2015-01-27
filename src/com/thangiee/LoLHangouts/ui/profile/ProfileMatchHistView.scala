package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.Match
import com.thangiee.LoLHangouts.domain.interactor.ViewProfileUseCaseImpl
import com.thangiee.LoLHangouts.ui.core.CustomView
import com.thangiee.LoLHangouts.ui.regionselection.RegionViewHolder
import com.thangiee.LoLHangouts.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.{ErrorView, RetryListener}

import scala.collection.JavaConversions._

class ProfileMatchHistView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val loadingWheel      = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView         = find[ErrorView](R.id.error_view)
  lazy val matchRecyclerView = find[RecyclerView](R.id.recycler_view)

  override val presenter = new ProfileMatchHistPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_match_hist, this, false))
    val llm = new LinearLayoutManager(ctx)
    llm.setSmoothScrollbarEnabled(true)
    matchRecyclerView.setVisibility(View.INVISIBLE)
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
    val adapter = new SimpleAdapter[Match](R.layout.line_item_match_history, classOf[MatchViewHolder], matches)

    delay(1500) { // wait for loading wheel to hide
      matchRecyclerView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[RegionViewHolder]])
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
    matchRecyclerView.slideInDown(delay = 1500)
  }

  def showLoadingError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 500) // delay in millis

    delay(1500) { // wait for loading wheel to hide
      errorView.setErrorTitle(title)
      errorView.setErrorSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}
