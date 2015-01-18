package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view.{ViewGroup, View}
import android.widget.FrameLayout
import com.thangiee.LoLHangouts.data.repository.userRepoImpl
import com.thangiee.LoLHangouts.domain.interactor.GetUserUseCaseImpl
import com.thangiee.LoLHangouts.{R, Container}
import com.thangiee.LoLHangouts.utils._
import it.neokree.materialtabs.{MaterialTab, MaterialTabListener, MaterialTabHost}

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileContainer(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  lazy val tabs                 = this.find[MaterialTabHost](R.id.tabs)
  lazy val pager                = this.find[ViewPager](R.id.pager)
  lazy val profileSummaryView   = this.find[ProfileSummaryView](R.id.page_1)
  lazy val profileTopChampView  = this.find[ProfileTopChampsView](R.id.page_2)
  lazy val profileMatchHistView = this.find[ProfileMatchHistView](R.id.page_3)

  case class Page(title: String, var isSet: Boolean = false)
  val loadUser = GetUserUseCaseImpl().loadUser()
  val pages    = List(Page("Summary", isSet = true), Page("Champions"), Page("History"))

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.view_pager, this, false))

    val pagerAdapter = new ViewPagerAdapter()
    pager.setOffscreenPageLimit(3)
    pager.setAdapter(pagerAdapter)
    pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
      override def onPageSelected(position: Int): Unit = {
        // when user do a swipe the selected tab change
        tabs.setSelectedNavigationItem(position)
        handleSwitchPage(position)
      }
    })

    (0 until pages.size).map { i =>
      tabs.addTab(tabs.newTab()
        .setText(pages(i).title)
        .setTabListener(this))
    }

    loadUser onSuccess { case user =>
      runOnUiThread(profileSummaryView.setProfile(user.inGameName, user.region.id)) // initialize the first page
    }
  }

  private def handleSwitchPage(position: Int): Unit = {
    // only load the page the user is currently viewing and initialize it only once
    if (!pages(position).isSet) {
      loadUser onSuccess { case user =>
        runOnUiThread {
          if (position == 1) {
            profileTopChampView.setProfile(user.inGameName, user.region.id)
            pages(1).isSet = true
          } else if (position == 2){
            profileMatchHistView.setProfile(user.inGameName, user.region.id)
            pages(2).isSet = true
          }
        }
      }
    }
  }

  override def getView: View = this

  override def onTabSelected(tab: MaterialTab): Unit = pager.setCurrentItem(tab.getPosition)

  override def onTabReselected(materialTab: MaterialTab): Unit = {}

  override def onTabUnselected(materialTab: MaterialTab): Unit = {}

  class ViewPagerAdapter extends PagerAdapter {

    override def instantiateItem(container: ViewGroup, position: Int): AnyRef = {
      position match {
        case 0 => profileSummaryView
        case 1 => profileTopChampView
        case 2 => profileMatchHistView
      }
    }

    override def destroyItem(container: ViewGroup, position: Int, `object`: scala.Any): Unit = {}

    override def getCount: Int = pages.size

    override def isViewFromObject(view: View, o: scala.Any): Boolean = {
      view == o.asInstanceOf[View]
    }
  }

}

