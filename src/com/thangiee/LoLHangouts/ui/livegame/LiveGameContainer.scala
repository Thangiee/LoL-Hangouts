package com.thangiee.LoLHangouts.ui.livegame

import android.content.Context
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.nispok.snackbar.Snackbar
import com.thangiee.LoLHangouts.activities.TActivity
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.interactor.ViewLiveGameUseCaseImpl
import com.thangiee.LoLHangouts.ui.livegame.LiveGameTeamView._
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{Container, R}
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class LiveGameContainer(username: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  lazy val tabs           = this.find[MaterialTabHost](R.id.tabs)
  lazy val pager          = this.find[ViewPager](R.id.pager)
  lazy val blueTeamView   = this.find[LiveGameTeamView](R.id.page_1)
  lazy val purpleTeamView = this.find[LiveGameTeamView](R.id.page_2)

  val viewLiveGameUseCase = ViewLiveGameUseCaseImpl()
  val pages               = List("Blue Team", "Purple Team")

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.view_live_game_screen, this, false))

    val pageChangeListener = new SimpleOnPageChangeListener() {
      override def onPageSelected(position: Int): Unit = {
        // when user do a swipe the selected tab change
        tabs.setSelectedNavigationItem(position)
      }
    }

    val pagerAdapter = new ViewPagerAdapter()
    pager.setAdapter(pagerAdapter)
    pager.setOnPageChangeListener(pageChangeListener)

    // set tabs title and listener
    (0 until pages.size).map { i =>
      tabs.addTab(tabs.newTab()
        .setText(pages(i))
        .setTabListener(this))
    }
    
    loadGame()
  }
  
  def loadGame(): Unit = {
    viewLiveGameUseCase.loadLiveGame(username, regionId) onComplete {
      case Success(liveGame) =>
        runOnUiThread {
          blueTeamView.initializeViewData(liveGame.blueTeam, BlueTeam)
          blueTeamView.hideLoading()
          purpleTeamView.initializeViewData(liveGame.purpleTeam, PurpleTeam)
          purpleTeamView.hideLoading()
        }
      case Failure(e)        =>
        runOnUiThread {
          Snackbar.`with`(ctx)
            .text("Failed to load live game")
            .actionLabel("Retry")
            .actionListener((snackbar: Snackbar) => reloadGame())
            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
            .show(ctx.asInstanceOf[TActivity])

          blueTeamView.showLoadingError("Oops", e.getMessage)
          purpleTeamView.showLoadingError("Oops", e.getMessage)
        }
    }
  }

  def reloadGame(): Unit = {
    blueTeamView.showLoading()
    purpleTeamView.showLoading()
    loadGame()
  }

  override def getView: View = this

  override def onTabSelected(tab: MaterialTab): Unit = pager.setCurrentItem(tab.getPosition)

  override def onTabReselected(materialTab: MaterialTab): Unit = {}

  override def onTabUnselected(materialTab: MaterialTab): Unit = {}

  class ViewPagerAdapter extends PagerAdapter {

    override def instantiateItem(container: ViewGroup, position: Int): AnyRef = {
      position match {
        case 0 => blueTeamView
        case 1 => purpleTeamView
      }
    }

    override def destroyItem(container: ViewGroup, position: Int, `object`: scala.Any): Unit = {}

    override def getCount: Int = pages.size

    override def isViewFromObject(view: View, o: scala.Any): Boolean = {
      view == o.asInstanceOf[View]
    }
  }

}


