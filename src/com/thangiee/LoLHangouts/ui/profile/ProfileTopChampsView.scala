package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.widget.{FrameLayout, ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.TopChampion
import com.thangiee.LoLHangouts.domain.interactor.ViewProfileUseCaseImpl
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{CustomView, R}
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import tr.xip.errorview.{RetryListener, ErrorView}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileTopChampsView(implicit ctx: Context, a: AttributeSet) extends FrameLayout(ctx, a) with CustomView {
  lazy val loadingWheel = find[CircularProgressBar](R.id.circular_loader)
  lazy val errorView    = find[ErrorView](R.id.error_view)
  lazy val champListView = find[ListView](R.id.listView)

  override val presenter = new ProfileTopChampsPresenter(this, ViewProfileUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.summoner_top_champ, null))
    champListView.setVisibility(View.INVISIBLE)
  }

  def setProfile(name: String, regionId: String) = {
    presenter.handleSetProfile(name, regionId)
    errorView.setOnRetryListener(new RetryListener {
      override def onRetry(): Unit = presenter.handleSetProfile(name, regionId)
    })
  }

  def initializeViewData(champions: List[TopChampion]): Unit = {
    val green = R.color.green.r2Color
    val red   = R.color.red.r2Color
    val champDict = new BindDictionary[TopChampion]()

    champDict.addStaticImageField(R.id.img_champ_icon, new StaticImageLoader[TopChampion] {
      override def loadImage(c: TopChampion, iv: ImageView, p3: Int): Unit =
        iv.setImageDrawable(ChampIconAssetFile(c.name).toDrawable)
    })

    champDict.addStringField(R.id.tv_champ_name, (c: TopChampion) => c.name)

    champDict.addStringField(R.id.tv_champ_perf, (c: TopChampion) => c.overAllPerformance + "%")
      .conditionalTextColor((c: TopChampion) => c.overAllPerformance >= 0, green, red)

    champDict.addStringField(R.id.tv_champ_game, (c: TopChampion) => c.numOfGames.toString)
    champDict.addStringField(R.id.tv_champ_win_rate, (c: TopChampion) => c.winsRate + "%")
      .conditionalTextColor((c: TopChampion) => c.winsRate >= 50, green, red)

    champDict.addStringField(R.id.tv_champ_avg_k, (c: TopChampion) => c.avgKills.toString)
    champDict.addStringField(R.id.tv_champ_avg_more_k, (c: TopChampion) => c.avgKillsPerformance.toString)
      .conditionalTextColor((c: TopChampion) => c.avgKillsPerformance >= 0, green, red)

    champDict.addStringField(R.id.tv_champ_avg_d, (c: TopChampion) => c.avgDeaths.toString)
    champDict.addStringField(R.id.tv_champ_avg_more_d, (c: TopChampion) => c.avgDeathsPerformance.toString)
      .conditionalTextColor((c: TopChampion) => c.avgDeathsPerformance <= 0, green, red)

    champDict.addStringField(R.id.tv_champ_avg_a, (c: TopChampion) => c.avgAssists.toString)
    champDict.addStringField(R.id.tv_champ_avg_more_a, (c: TopChampion) => c.avgAssistsPerformance.toString)
      .conditionalTextColor((c: TopChampion) => c.avgAssistsPerformance >= 0, green, red)

    champDict.addStringField(R.id.tv_champ_avg_cs, (c: TopChampion) => c.avgCs.toString)
    champDict.addStringField(R.id.tv_champ_avg_more_cs, (c: TopChampion) => c.avgCsPerformance.toString)
      .conditionalTextColor((c: TopChampion) => c.avgCsPerformance >= 0, green, red)

    champDict.addStringField(R.id.tv_champ_avg_g, (c: TopChampion) => c.avgGold.toString)
    champDict.addStringField(R.id.tv_champ_avg_more_g, (c: TopChampion) => c.avgGoldPerformance.toString)
      .conditionalTextColor((c: TopChampion) => c.avgGoldPerformance >= 0, green, red)

    val adapter = new FunDapter[TopChampion](ctx, champions, R.layout.champion_stats, champDict)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)

    Future {
      SystemClock.sleep(1500) // wait for loading wheel to hide
      runOnUiThread(champListView.setAdapter(adapter))
    }
  }

  def showLoading(): Unit = {
    loadingWheel.setVisibility(View.VISIBLE)
    errorView.setVisibility(View.GONE)
    loadingWheel.fadeInDown(duration = 1)
    champListView.setVisibility(View.INVISIBLE)
  }

  def hideLoading(): Unit = {
    loadingWheel.zoomOut(delay = 500)
    champListView.slideInDown(delay = 1500)
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
