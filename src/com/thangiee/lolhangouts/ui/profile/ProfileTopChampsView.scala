package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.TopChampion
import com.thangiee.lolhangouts.data.usecases.ViewProfileUseCaseImpl
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.utils._
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.ErrorView
import tr.xip.errorview.ErrorView.RetryListener

import scala.collection.JavaConversions._

class ProfileTopChampsView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  private lazy val loadingWheel = find[CircularProgressBar](R.id.circular_loader)
  private lazy val errorView    = find[ErrorView](R.id.error_view)
  private lazy val champRecView = find[RecyclerView](R.id.rv_suggestions)

  override protected val presenter = new ProfileTopChampsPresenter(this, ViewProfileUseCaseImpl())

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

    delay(500) { // wait for loading wheel to hide
      champRecView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[TopChampViewHolder]])
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
    champRecView.fadeIn(delay = 500)
  }

  def showDataNotFound(): Unit = showError("No Result", R.string.no_champion.r2String)

  def showGetDataError(): Unit = showError(
    title = (if (hasWifiConnection) R.string.server_busy else R.string.no_wifi).r2String,
    subTitle = R.string.err_get_data.r2String
  )

  private def showError(title: String, subTitle: String): Unit = {
    loadingWheel.zoomOut(delay = 500) // delay in millis

    delay(1500) {
      errorView.setTitle(title)
      errorView.setSubtitle(subTitle)
      errorView.setVisibility(View.VISIBLE)
    }
  }
}
