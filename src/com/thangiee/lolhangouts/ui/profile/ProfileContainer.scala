package com.thangiee.lolhangouts.ui.profile

import android.content.Context
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener
import android.support.v4.view.{PagerAdapter, ViewPager}
import android.view._
import android.widget.FrameLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.{ManageFriendUseCaseImpl, GetFriendsUseCaseImpl, GetUserUseCaseImpl}
import com.thangiee.lolhangouts.ui.core.Container
import com.thangiee.lolhangouts.ui.utils._
import it.neokree.materialtabs.{MaterialTab, MaterialTabHost, MaterialTabListener}

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileContainer(name: String, regionId: String)(implicit ctx: Context) extends FrameLayout(ctx) with Container with MaterialTabListener {
  private lazy val tabs                 = this.find[MaterialTabHost](R.id.tabs)
  private lazy val pager                = this.find[ViewPager](R.id.pager)
  private lazy val profileSummaryView   = this.find[ProfileSummaryView](R.id.page_1)
  private lazy val profileTopChampView  = this.find[ProfileTopChampsView](R.id.page_2)
  private lazy val profileMatchHistView = this.find[ProfileMatchHistView](R.id.page_3)

  case class Page(title: String, var isSet: Boolean = false)

  private val loadUser = GetUserUseCaseImpl().loadUser()
  private val pages    = List(Page("Summary"), Page("Champions"), Page("History"))
  private var pagePosition = 0

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

    pages.indices.foreach { i =>
      tabs.addTab(tabs.newTab()
        .setText(pages(i).title)
        .setTabListener(this))
    }

    delay(150) { // make sure view is attached first
      pageChangeListener.onPageSelected(0)
    }
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menuInflater.inflate(R.menu.overflow, menu)

    // determine to inflate an add friend menu btn or not
    GetFriendsUseCaseImpl().loadFriendByName(name) onSuccess { case Bad(_) => // not in friend list
      loadUser onSuccess { case Good(user) =>
        // don't inflate if viewing your own profile or a profile from a different region
        if (name.toLowerCase != user.inGameName.toLowerCase && regionId.toLowerCase == user.region.id.toLowerCase)
          runOnUiThread(menuInflater.inflate(R.menu.add_friend, menu))
      }
    }

    if (pagePosition == 1 || pagePosition == 2) {
      menuInflater.inflate(R.menu.info, menu)
    }
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_add_friend =>
        ManageFriendUseCaseImpl().sendFriendRequest(name)
        // todo: show message
        true
      case R.id.menu_info       =>
        if (pagePosition == 1) {
          new MaterialDialog.Builder(ctx)
            .title("Top Ranked Champion")
            .customView(R.layout.info_top_champs, true)
            .positiveText(android.R.string.ok)
            .show()
        } else {
          new MaterialDialog.Builder(ctx)
            .title("Match History")
            .customView(R.layout.info_match_hist, true)
            .positiveText(android.R.string.ok)
            .show()
        }
        true
      case _                    => false
    }
  }

  private def handleSwitchPage(position: Int): Unit = {
    // stop onCreateOptionsMenu from being call twice after initialization
    if (position != pagePosition) {
      pagePosition = position
      invalidateOptionsMenu()
    }

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

