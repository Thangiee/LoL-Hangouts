package com.thangiee.lolhangouts.ui.scoutgame

import android.content.Context
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.core.Container
import com.thangiee.lolhangouts.ui.scoutgame.ScoutGameView._
import com.thangiee.lolhangouts.ui.utils._
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

class ScoutGameContainer(username: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  private lazy val tabs           = this.find[MaterialTabHost](R.id.tabs)
  private lazy val pager          = this.find[ViewPager](R.id.pager)
  private lazy val blueTeamView   = this.find[ScoutGameView](R.id.page_1)
  private lazy val purpleTeamView = this.find[ScoutGameView](R.id.page_2)

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

    blueTeamView.scoutGame(username, regionId, BlueTeam)
    delay(1500) {
      // avoid double work and relay on the cached scout report done by Blue team ScoutGamePresenter
      purpleTeamView.scoutGame(username, regionId, PurpleTeam)
    }
  }

  override def onDetachedFromWindow(): Unit = {
    super.onDetachedFromWindow()
    toolbar.setSubtitle(null)
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


