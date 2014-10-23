package com.thangiee.LoLHangouts.fragments

import android.app.{Fragment, FragmentManager}
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view._
import com.astuetz.PagerSlidingTabStrip
import com.devspark.progressfragment.ProgressFragment
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.api.stats.{LoLSkill, ProfilePlayerStats}
import com.thangiee.LoLHangouts.api.utils.RiotApi
import com.thangiee.LoLHangouts.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

case class ProfileViewPagerFragment() extends ProgressFragment with TFragment {
  private lazy val tabs = find[PagerSlidingTabStrip](R.id.tabs)
  private lazy val pager = find[ViewPager](R.id.pager)
  private lazy val adapter = new MyPagerAdapter(getFragmentManager)
  private lazy val name = getArguments.getString("name-key")
  private lazy val region = getArguments.getString("region-key")
  private var userStats: ProfilePlayerStats = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.view_pager, null)
    setHasOptionsMenu(true)
    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    setContentView(view)
    pager.setAdapter(adapter)
    tabs.setViewPager(pager)
    loadData()
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    menu.clear()
    inflater.inflate(R.menu.overflow, menu)
    inflater.inflate(R.menu.refresh, menu)
    if (LoLChat.getFriendByName(name).isEmpty) {  // not in friend list
      // don't inflate if viewing your own profile or a profile from a different region
      if (name.toLowerCase != appCtx.currentUser.toLowerCase && region.toLowerCase == appCtx.selectedRegion.id)
        inflater.inflate(R.menu.add_friend, menu)
    }
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_refresh => loadData(); true
      case R.id.menu_add_friend => addFriend(); true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  private def addFriend(): Unit = {
    Future {
      RiotApi.getSummonerId(name) match {
        case Some(id) => LoLChat.connection.getRoster.createEntry(s"sum$id@pvp.net", name, null)
        case None => "Failed to add friend".croutonWarn()
      }
    }
  }

  private def loadData(): Unit = {
    setContentShown(false) // show loading bar
    Future {
      try {
        userStats = new LoLSkill(name, region)
        runOnUiThread {
          Try {
            setContentEmpty(false) // hide error msg if currently showing
            setContentShown(true) // hide loading bar
          } match {
            case Success(_) ⇒
            case Failure(_) ⇒ userStats = null
          }
        }
        info("[+] Got user stats successfully")
      } catch {
        case e: Exception ⇒ runOnUiThread {
          warn("[!] Failed to get user stats because: " + e.getMessage)
          Try {
            R.string.connection_error_short.r2String.croutonWarn()
            setEmptyText(R.string.connection_error_long.r2String)
            runOnUiThread {
              setContentEmpty(true) // show error msg
              setContentShown(true) // hide loading bar
            }
          } match {
            case Success(_) ⇒
            case Failure(_) ⇒ userStats = null
          }
        }
      }
    }
  }

  class MyPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
    private val titles = List("Profile", "Champions", "History")

    override def getPageTitle(position: Int): CharSequence = titles(position)

    override def getItem(position: Int): Fragment = {
      titles(position) match {
        case "Profile"    ⇒ SummonerProfileFragment(userStats)
        case "Champions"  ⇒ if (userStats.topChampions.size != 0) SummonerTopChampFragment(userStats.topChampions)
                            else BlankFragment(R.string.no_champion)
        case "History"    ⇒ if (userStats.matchHistory.size != 0) SummonerMatchesFragment(userStats.matchHistory)
                            else BlankFragment(R.string.no_match_hist)
      }
    }
    override def getCount: Int = titles.size
  }
}

object ProfileViewPagerFragment {
  def apply(summonerName: String, region: String): ProfileViewPagerFragment = {
    ProfileViewPagerFragment().args("name-key" → summonerName, "region-key" → region)
  }
}
