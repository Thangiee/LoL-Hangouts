package com.thangiee.lolhangouts.ui.livegame

import android.content.Context
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.nispok.snackbar.Snackbar
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.exception.DataAccessException
import com.thangiee.lolhangouts.data.exception.DataAccessException._
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCaseImpl
import com.thangiee.lolhangouts.ui.core.{Container, TActivity}
import com.thangiee.lolhangouts.ui.livegame.GameScouterTeamView._
import com.thangiee.lolhangouts.ui.utils._
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class GameScouterContainer(username: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  lazy val tabs           = this.find[MaterialTabHost](R.id.tabs)
  lazy val pager          = this.find[ViewPager](R.id.pager)
  lazy val blueTeamView   = this.find[GameScouterTeamView](R.id.page_1)
  lazy val purpleTeamView = this.find[GameScouterTeamView](R.id.page_2)

  val viewLiveGameUseCase = ScoutGameUseCaseImpl()
  val pages               = List("Blue Team", "Purple Team")

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.view_game_scouter_screen, this, false))

    val pageChangeListener = new SimpleOnPageChangeListener() {
      override def onPageSelected(position: Int): Unit = {
        // when user do a swipe the selected tab change
        tabs.setSelectedNavigationItem(position)
      }
    }

    val pagerAdapter = new ViewPagerAdapter()
    pager.setAdapter(pagerAdapter)
    pager.setOnPageChangeListener(pageChangeListener)

    // set tabs title and listener for all pages
    (0 until pages.size).map { i =>
      tabs.addTab(tabs.newTab()
        .setText(pages(i))
        .setTabListener(this))
    }
    
    loadGame()
  }

  override def onDetachedFromWindow(): Unit = {
    super.onDetachedFromWindow()
    toolbar.setSubtitle(null)
  }

  def loadGame(): Unit = {
    viewLiveGameUseCase.loadGameInfo(username, regionId) onComplete {
      case Success(liveGame) =>
        runOnUiThread {
          setToolbarTitle(s"${liveGame.mapName} - $regionId")
          blueTeamView.initializeViewData(liveGame.blueTeam, BlueTeam)
          blueTeamView.hideLoading()
          purpleTeamView.initializeViewData(liveGame.purpleTeam, PurpleTeam)
          purpleTeamView.hideLoading()
        }
      case Failure(e)        =>
        runOnUiThread {
          Snackbar.`with`(ctx)
            .text("Failed to load game")
            .textColorResource(R.color.md_white)
            .colorResource(R.color.md_grey_900)
            .actionLabel("Retry")
            .actionColorResource(R.color.accent_light)
            .actionListener((snackbar: Snackbar) => reloadGame())
            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
            .show(ctx.asInstanceOf[TActivity])

          val errMsg = e match {
            case DataAccessException(_, DataNotFound) => username + R.string.not_in_game.r2String
            case DataAccessException(_, GetDataError) => R.string.err_get_data.r2String
          }

          blueTeamView.showLoadingError("Oops", errMsg)
          purpleTeamView.showLoadingError("Oops", errMsg)
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

