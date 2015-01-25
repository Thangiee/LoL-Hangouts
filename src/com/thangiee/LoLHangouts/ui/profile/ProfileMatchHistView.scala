package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.os.SystemClock
import android.support.v7.graphics.Palette
import android.support.v7.graphics.Palette.PaletteAsyncListener
import android.util.AttributeSet
import android.view.View
import android.widget.{FrameLayout, ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.Match
import com.thangiee.LoLHangouts.domain.interactor.ViewProfileUseCaseImpl
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{CustomView, R}
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.{ErrorView, RetryListener}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileMatchHistView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val loadingWheel  = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView     = find[ErrorView](R.id.error_view)
  lazy val matchListView = find[ListView](R.id.listView)

  override val presenter = new ProfileMatchHistPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.profile_match_hist, this, false))
    matchListView.setVisibility(View.INVISIBLE)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(new RetryListener {
      override def onRetry(): Unit = presenter.handleSetProfile(name, regionId)
    })
  }

  def initializeViewData(matches: List[Match]): Unit = {
    val green = R.color.md_light_green_600.r2Color
    val red   = R.color.red.r2Color
    val matchDict = new BindDictionary[Match]()

    matchDict.addStaticImageField(R.id.img_match_champ, new StaticImageLoader[Match] {
      override def loadImage(m: Match, iv: ImageView, p3: Int): Unit =
        iv.setImageDrawable(ChampIconAssetFile(m.champName).toDrawable)
    })

    matchDict.addStaticImageField(R.id.rect, new StaticImageLoader[Match] {
      override def loadImage(m: Match, iv: ImageView, i: Int): Unit = {
        Palette.generateAsync(ChampIconAssetFile(m.champName).toBitmap, new PaletteAsyncListener {
          override def onGenerated(palette: Palette): Unit = iv.setBackgroundColor(palette.getVibrantColor(R.color.primary.r2Color))
        })
      }
    })

    matchDict.addStringField(R.id.tv_match_type, (m: Match) ⇒ m.queueType)

    matchDict.addStringField(R.id.tv_match_outcome, (m: Match) ⇒ m.outCome)
      .conditionalTextColor((m: Match) ⇒ m.outCome.toLowerCase.equals("win"), green, red)

    matchDict.addStringField(R.id.tv_match_date, (m: Match) ⇒ m.date)

    matchDict.addStringField(R.id.tv_match_len, (m: Match) ⇒ m.duration.replace("≈", ""))

    matchDict.addStringField(R.id.tv_match_perf, (m: Match) ⇒ m.overAllPerformance + "%")
      .conditionalTextColor((m: Match) ⇒ m.overAllPerformance >= 0, green, red)

    matchDict.addStringField(R.id.tv_match_avg_k, (m: Match) ⇒ m.avgKills.toString)
    matchDict.addStringField(R.id.tv_match_avg_more_k, (m: Match) ⇒ m.avgKillsPerformance.toString)
      .conditionalTextColor((m: Match) ⇒ m.avgKillsPerformance >= 0, green, red)

    matchDict.addStringField(R.id.tv_match_avg_d, (m: Match) ⇒ m.avgDeaths.toString)
    matchDict.addStringField(R.id.tv_match_avg_more_d, (m: Match) ⇒ m.avgDeathsPerformance.toString)
      .conditionalTextColor((m: Match) ⇒ m.avgDeathsPerformance <= 0, green, red)

    matchDict.addStringField(R.id.tv_match_avg_a, (m: Match) ⇒ m.avgAssists.toString)
    matchDict.addStringField(R.id.tv_match_avg_more_a, (m: Match) ⇒ m.avgAssistsPerformance.toString)
      .conditionalTextColor((m: Match) ⇒ m.avgAssistsPerformance >= 0, green, red)

    matchDict.addStringField(R.id.tv_match_avg_cs, (m: Match) ⇒ m.avgCs.toString)
    matchDict.addStringField(R.id.tv_match_avg_more_cs, (m: Match) ⇒ m.avgCsPerformance.toString)
      .conditionalTextColor((m: Match) ⇒ m.avgCsPerformance >= 0, green, red)

    val adapter = new FunDapter[Match](ctx, matches, R.layout.line_item_match_history, matchDict)

    Future {
      SystemClock.sleep(1500) // wait for loading wheel to hide
      runOnUiThread(matchListView.setAdapter(adapter))
    }
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    errorView.setVisibility(View.GONE)
    loadingWheel.fadeInDown(duration = 1)
    matchListView.setVisibility(View.INVISIBLE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 500)
    matchListView.slideInDown(delay = 1500)
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
