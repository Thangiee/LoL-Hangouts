package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.os.SystemClock
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.TopChampion
import com.thangiee.LoLHangouts.domain.interactor.ViewProfileUseCaseImpl
import com.thangiee.LoLHangouts.ui.core.CustomView
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.R
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.{ErrorView, RetryListener}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileTopChampsView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val loadingWheel = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView    = find[ErrorView](R.id.error_view)
  lazy val champRecView = find[RecyclerView](R.id.recycler_view)

  override val presenter = new ProfileTopChampsPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_top_champs, this, false))
    val llm = new LinearLayoutManager(ctx)
    llm.setSmoothScrollbarEnabled(true)
    champRecView.setVisibility(View.INVISIBLE)
    champRecView.setVerticalScrollBarEnabled(true)
    champRecView.setLayoutManager(llm)
    champRecView.setHasFixedSize(true)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(new RetryListener {
      override def onRetry(): Unit = presenter.handleSetProfile(name, regionId)
    })
  }

  def initializeViewData(champions: List[TopChampion]): Unit = {
    val adapter = new SimpleAdapter[TopChampion](R.layout.line_item_top_champ, classOf[TopChampViewHolder], champions)

    Future {
      SystemClock.sleep(1500) // wait for loading wheel to hide
      runOnUiThread(champRecView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[TopChampViewHolder]]))
    }
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    errorView.setVisibility(View.GONE)
    loadingWheel.fadeInDown(duration = 1)
    champRecView.setVisibility(View.INVISIBLE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 500)
    champRecView.slideInDown(delay = 1500)
  }

  def showLoadingError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 500) // delay in millis

    Future {
      SystemClock.sleep(1500) // wait for loading wheel to hide
      runOnUiThread {
        errorView.setErrorTitle(title)
        errorView.setErrorSubtitle(subTitle)
        errorView.setVisibility(View.VISIBLE)
      }
    }
  }
}
