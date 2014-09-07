package com.thangiee.LoLHangouts.fragments

import android.app.{Fragment, FragmentManager}
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view._
import com.astuetz.PagerSlidingTabStrip
import com.devspark.progressfragment.ProgressFragment
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.{LiveGameStats, RiotLiveStats}
import com.thangiee.LoLHangouts.api.utils.RiotApi
import com.thangiee.LoLHangouts.fragments.LiveGameTeamFragment.{BLUE_TEAM, PURPLE_TEAM}
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LiveGamePagerFragment extends ProgressFragment with TFragment with OnPageChangeListener{
  private lazy val tabs = find[PagerSlidingTabStrip](R.id.tabs)
  private lazy val pager = find[ViewPager](R.id.pager)
  private lazy val adapter = new MyPagerAdapter(getFragmentManager)
  private lazy val name = getArguments.getString("name-key")
  private lazy val region = getArguments.getString("region-key")
  private var liveGame: LiveGameStats = _
  private val mapNames = Map[Int, String](
    1 → "Summoner's Rift",
    2 → "Summoner's Rift",
    3 → "The Proving Grounds",
    4 → "Twisted Treeline",
    8 → "The Crystal Scar",
    10 → "Twisted Treeline",
    12 → "Howling Abyss"
  )

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
    tabs.setOnPageChangeListener(this)
    loadData()
  }

  override def onStop(): Unit = {
    RiotApi.setRegion(appCtx.selectedRegion.id)
    Crouton.cancelAllCroutons()
    super.onStop()
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    menu.clear()
    inflater.inflate(R.menu.overflow, menu)
    inflater.inflate(R.menu.refresh, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (item.getItemId == R.id.menu_refresh) {
      Crouton.cancelAllCroutons()
      loadData()
      return true
    }
    super.onOptionsItemSelected(item)
  }

  def loadData(): Unit = {
    setContentShown(false) // show loading bar
    Future {
      try {
        liveGame = new RiotLiveStats(name, region)
        runOnUiThread {
          setContentEmpty(false) // hide error msg if currently showing
          setContentShown(true) // hide loading bar
          getActivity.getActionBar.setTitle(liveGame.queueType.replace("_", " "))
          getActivity.getActionBar.setSubtitle(mapNames.getOrElse(liveGame.mapId, ""))
        }
        info("[+] Got live game successfully")
      } catch {
        case e: Exception ⇒ runOnUiThread {
          warn("[!] Failed to get user stats because: " + e.getMessage)
          e.getMessage.makeCrouton(Style.ALERT, Configuration.DURATION_INFINITE)
          setEmptyText("")
          runOnUiThread {
            setContentEmpty(true) // show error msg
            setContentShown(true) // hide loading bar
          }
        }
      }
    }
  }

  override def onPageScrolled(p1: Int, p2: Float, p3: Int): Unit = {}

  override def onPageScrollStateChanged(position: Int): Unit = {}

  override def onPageSelected(position: Int): Unit = {
    var color = android.R.color.holo_blue_dark
    if (position == PURPLE_TEAM) color = android.R.color.holo_purple
    tabs.setIndicatorColorResource(color)
  }

  class MyPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
    private val titles = List("Blue Team", "Purple Team")

    override def getPageTitle(position: Int): CharSequence = titles(position)

    override def getItem(position: Int): Fragment = {
      position match {
        case BLUE_TEAM    ⇒ LiveGameTeamFragment.newInstance(liveGame.blueTeam, BLUE_TEAM, region)
        case PURPLE_TEAM  ⇒ LiveGameTeamFragment.newInstance(liveGame.purpleTeam, PURPLE_TEAM, region)
      }
    }

    override def getCount: Int = titles.size
  }
}

object LiveGamePagerFragment {
  def newInstance(summonerName: String, region: String): LiveGamePagerFragment = {
    val bundle = new Bundle()
    bundle.putString("name-key", summonerName)
    bundle.putString("region-key", region)
    val frag = new LiveGamePagerFragment
    frag.setArguments(bundle)
    frag
  }
}