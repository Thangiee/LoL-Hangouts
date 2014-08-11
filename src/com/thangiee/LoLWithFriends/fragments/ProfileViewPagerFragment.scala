package com.thangiee.LoLWithFriends.fragments

import android.app.{Fragment, FragmentManager}
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.{LayoutInflater, View, ViewGroup}
import com.astuetz.PagerSlidingTabStrip
import com.devspark.progressfragment.ProgressFragment
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.{LoLSkill, LoLStatistics}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ProfileViewPagerFragment extends ProgressFragment with SFragment {
  private lazy val tabs = find[PagerSlidingTabStrip](R.id.tabs)
  private lazy val pager = find[ViewPager](R.id.pager)
  private lazy val adapter = new MyPagerAdapter(getFragmentManager)
  private lazy val name = getArguments.getString("name-key")
  private lazy val region = getArguments.getString("region-key")
  private var userStats: LoLStatistics = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.view_pager_profile, null)

    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    setContentView(view)
    pager.setAdapter(adapter)
    tabs.setViewPager(pager)
    setContentShown(false) // show loading bar
    Future {
      try {
        userStats = new LoLSkill(name, region)
        runOnUiThread(setContentShown(true))
        info("[*] Got user stats successfully")
      } catch {
        case e: Exception ⇒ runOnUiThread {
          warn("[*] Failed to get user stats because: " + e.getMessage)
          setEmptyText("Could not get data.")
          setContentEmpty(true)
          setContentShown(true)
        }
      }
    }
  }

  class MyPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
    private val titles = List("Profile", "Champions", "History")

    override def getPageTitle(position: Int): CharSequence = titles(position)

    override def getItem(position: Int): Fragment = {
      titles(position) match {
        case "Profile" ⇒ SummonerProfileFragment.newInstance(name, userStats)
        case "Champions" ⇒ SummonerTopChampFragment.newInstance(userStats.topChampions())
        case "History" ⇒ SummonerMatchesFragment.newInstance(userStats.matchHistory())
      }
    }

    override def getCount: Int = titles.size
  }

}


object ProfileViewPagerFragment {
  def newInstance(summonerName: String, region: String): ProfileViewPagerFragment = {
    val bundle = new Bundle()
    bundle.putString("name-key", summonerName)
    bundle.putString("region-key", region)
    val frag = new ProfileViewPagerFragment
    frag.setArguments(bundle)
    frag
  }
}
