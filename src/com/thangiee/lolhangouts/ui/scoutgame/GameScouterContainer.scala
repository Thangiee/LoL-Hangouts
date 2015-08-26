package com.thangiee.lolhangouts.ui.scoutgame

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCase.{GameInfoNotFound, InternalError}
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCaseImpl
import com.thangiee.lolhangouts.ui.core.Container
import com.thangiee.lolhangouts.ui.scoutgame.GameScouterTeamView._
import com.thangiee.lolhangouts.ui.utils._
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

import scala.concurrent.ExecutionContext.Implicits.global

class GameScouterContainer(username: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  private lazy val tabs           = this.find[MaterialTabHost](R.id.tabs)
  private lazy val pager          = this.find[ViewPager](R.id.pager)
  private lazy val blueTeamView   = this.find[GameScouterTeamView](R.id.page_1)
  private lazy val purpleTeamView = this.find[GameScouterTeamView](R.id.page_2)

  private val viewLiveGameUseCase = ScoutGameUseCaseImpl()
  private val pages               = List("Blue Team", "Purple Team")

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
    pages.indices.foreach { i =>
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
    viewLiveGameUseCase.loadGameInfo(username, regionId).onSuccess {
      case Good(gameInfo) => runOnUiThread {
        setToolbarTitle(s"${gameInfo.mapName} - $regionId")
        blueTeamView.initializeViewData(gameInfo.blueTeam, BlueTeam)
        blueTeamView.hideLoading()
        purpleTeamView.initializeViewData(gameInfo.purpleTeam, PurpleTeam)
        purpleTeamView.hideLoading()
      }
      case Bad(e) => runOnUiThread {
        SnackBar("Failed to load game")
          .setDuration(Snackbar.LENGTH_INDEFINITE)
          .setAction("Reload", reloadGame())
          .show()

        val errMsg = e match {
          case GameInfoNotFound => username + R.string.not_in_game.r2String
          case InternalError    => R.string.err_get_data.r2String
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


