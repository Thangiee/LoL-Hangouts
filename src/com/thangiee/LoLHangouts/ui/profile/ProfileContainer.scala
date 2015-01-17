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
  lazy val tabs = this.find[MaterialTabHost](R.id.tabs)
  lazy val pager = this.find[ViewPager](R.id.pager)
  lazy val profileSummaryView = this.find[ProfileSummaryView](R.id.page_1)

  val getUserUseCase = GetUserUseCaseImpl()

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.view_pager, null))

    val pagerAdapter = new ViewPagerAdapter()
    pager.setOffscreenPageLimit(3)
    pager.setAdapter(pagerAdapter)
    pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
      override def onPageSelected(position: Int): Unit = {
        // when user do a swipe the selected tab change
        tabs.setSelectedNavigationItem(position)
      }
    })

    (0 until pagerAdapter.getCount).map { i =>
      tabs.addTab(tabs.newTab().setText("Test" + i).setTabListener(this))
    }

    getUserUseCase.loadUser().map { user =>
      runOnUiThread(profileSummaryView.setProfile(user.inGameName, user.region.id))
    }
  }

  override def getView: View = this

  override def onTabSelected(tab: MaterialTab): Unit = {
    pager.setCurrentItem(tab.getPosition)
  }

  override def onTabReselected(materialTab: MaterialTab): Unit = {}

  override def onTabUnselected(materialTab: MaterialTab): Unit = {}

  class ViewPagerAdapter extends PagerAdapter {

    override def instantiateItem(container: ViewGroup, position: Int): AnyRef = {
      position match {
        case 0 => findViewById(R.id.page_1)
        case 1 => findViewById(R.id.page_2)
        case 2 => findViewById(R.id.page_3)
      }
    }

    override def destroyItem(container: ViewGroup, position: Int, `object`: scala.Any): Unit = {}

    override def getCount: Int = 3

    override def isViewFromObject(view: View, o: scala.Any): Boolean = {
      view == o.asInstanceOf[View]
    }
  }
}

