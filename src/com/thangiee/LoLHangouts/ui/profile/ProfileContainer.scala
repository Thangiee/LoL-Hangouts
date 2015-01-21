package com.thangiee.LoLHangouts.ui.profile

import android.content.Context
import android.os.SystemClock
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view._
import android.widget.FrameLayout
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.interactor.{AddFriendUseCaseImpl, GetFriendsUseCaseImpl, GetUserUseCaseImpl}
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{Container, R}
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ProfileContainer(name: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  lazy val tabs                 = this.find[MaterialTabHost](R.id.tabs)
  lazy val pager                = this.find[ViewPager](R.id.pager)
  lazy val profileSummaryView   = this.find[ProfileSummaryView](R.id.page_1)
  lazy val profileTopChampView  = this.find[ProfileTopChampsView](R.id.page_2)
  lazy val profileMatchHistView = this.find[ProfileMatchHistView](R.id.page_3)

  case class Page(title: String, var isSet: Boolean = false)

  val loadUser = GetUserUseCaseImpl().loadUser()
  val pages    = List(Page("Summary"), Page("Champions"), Page("History"))

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.view_profile_screen, this, false))

    val pageChangeListener = new SimpleOnPageChangeListener() {
      override def onPageSelected(position: Int): Unit = {
        // when user do a swipe the selected tab change
        tabs.setSelectedNavigationItem(position)
        handleSwitchPage(position)
      }
    }

    val pagerAdapter = new ViewPagerAdapter()
    pager.setOffscreenPageLimit(3)
    pager.setAdapter(pagerAdapter)
    pager.setOnPageChangeListener(pageChangeListener)

    (0 until pages.size).map { i =>
      tabs.addTab(tabs.newTab()
        .setText(pages(i).title)
        .setTabListener(this))
    }

    Future {
      while (!profileSummaryView.isAttachedToWindow) {
        SystemClock.sleep(100)
      } // make sure view is attached first
      runOnUiThread(pageChangeListener.onPageSelected(0))
    }
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menuInflater.inflate(R.menu.overflow, menu)
    GetFriendsUseCaseImpl().loadFriendByName(name) onFailure { case e => // not in friend list
      loadUser onSuccess { case user =>
        // don't inflate if viewing your own profile or a profile from a different region
        if (name.toLowerCase != user.inGameName.toLowerCase && regionId.toLowerCase == user.region.id.toLowerCase)
          runOnUiThread(menuInflater.inflate(R.menu.add_friend, menu))
      }
    }
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_add_friend =>
        AddFriendUseCaseImpl().addFriend(name) onComplete {
          case Success(_)  => info(s"[+] $name added to friend list")
          case Failure(e) => "Failed to add friend".croutonWarn()
        }
        true
      case _                    => false
    }
  }

  private def handleSwitchPage(position: Int): Unit = {
    // only load the page the user is currently viewing and initialize it only once
    if (!pages(position).isSet) {
      position match {
        case 0 => profileSummaryView.setProfile(name, regionId)
        case 1 => profileTopChampView.setProfile(name, regionId)
        case 2 => profileMatchHistView.setProfile(name, regionId)
      }
      pages(position).isSet = true
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

