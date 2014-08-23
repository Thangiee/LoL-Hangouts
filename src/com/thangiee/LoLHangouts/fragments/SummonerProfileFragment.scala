package com.thangiee.LoLHangouts.fragments

import java.text.DecimalFormat

import android.os.{Bundle, SystemClock}
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, TextView}
import com.echo.holographlibrary.{PieGraph, PieSlice}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.ProfilePlayerStats
import com.thangiee.LoLHangouts.fragments.SummonerProfileFragment.Data
import com.thangiee.LoLHangouts.utils.SummonerUtils

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SummonerProfileFragment extends TFragment {
  private lazy val pieGraph = find[PieGraph](R.id.pie_graph_win_rate)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.summoner_profile, container, false)
    val data = getArguments.getSerializable("data").asInstanceOf[Data]
    val percent = (data.win / (data.win + data.lose).toDouble) * 100

    find[TextView](R.id.tv_profile_name).setText(data.name)
    find[TextView](R.id.tv_profile_lvl).setText("Level " + data.level.toString)
    find[TextView](R.id.tv_profile_tier).setText(data.leagueTier + " " + data.leagueDivision)
    find[TextView](R.id.tv_profile_league_name).setText(data.leagueName)
    find[TextView](R.id.tv_profile_league_point).setText(data.leaguePoints)
    find[TextView](R.id.tv_profile_kda).setText(data.kda)
    find[TextView](R.id.tv_profile_lost).setText(data.lose.toString + "L")
    find[TextView](R.id.tv_profile_win).setText(data.win.toString + "W")
    SummonerUtils.loadIconInto(getActivity, data.name, find[ImageView](R.id.img_profile_icon))

    val rateTextView = find[TextView](R.id.tv_profile_rate)
    rateTextView.setText(new DecimalFormat("###.##").format(percent) + "%")
    rateTextView.setTextColor(if (percent >= 50) android.R.color.holo_green_dark.r2Color else R.color.red.r2Color)

    val badgeImageView = find[ImageView](R.id.img_profile_badge)
    data.leagueTier.toUpperCase match {
      case "BRONZE"   ⇒ badgeImageView.setImageResource(R.drawable.badge_bronze)
      case "SILVER"   ⇒ badgeImageView.setImageResource(R.drawable.badge_silver)
      case "GOLD"     ⇒ badgeImageView.setImageResource(R.drawable.badge_gold)
      case "DIAMOND"  ⇒ badgeImageView.setImageResource(R.drawable.badge_diamond)
      case "PLATINUM" ⇒ badgeImageView.setImageResource(R.drawable.badge_platinum)
      case "CHALLENGER" ⇒ badgeImageView.setImageResource(R.drawable.badge_challenger)
      case _          ⇒ badgeImageView.setImageResource(R.drawable.badge_unranked)
    }

    // setup the pie graph
    val winSlice = new PieSlice
    winSlice.setColor(android.R.color.holo_green_dark.r2Color)
    winSlice.setValue(5)
    winSlice.setGoalValue(data.win)
    pieGraph.addSlice(winSlice)

    val loseSlice = new PieSlice
    loseSlice.setColor(R.color.red.r2Color)
    loseSlice.setValue(5)
    loseSlice.setGoalValue(data.lose)
    pieGraph.addSlice(loseSlice)

    pieGraph.setPadding(2)
    pieGraph.setInnerCircleRatio(R.integer.inner_circle_ratio.r2Integer)
    pieGraph.setInterpolator(new AccelerateDecelerateInterpolator())
    pieGraph.setDuration(1000)

    view
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    // delay starting the animation
    Future {
      if (!pieGraph.getSlices.forall(slice ⇒ slice.getGoalValue == 0)) {  // dont animate if 0 win and 0 lose
        SystemClock.sleep(1000)
        runOnUiThread(pieGraph.animateToGoalValues())
      }
    }
  }
}

object SummonerProfileFragment {
  def newInstance(summonerName: String, stats: ProfilePlayerStats): SummonerProfileFragment = {
    val bundle = new Bundle()
    val data = Data(
      summonerName, stats.kda(stats.soloQueue), stats.leagueDivision, stats.leagueName, stats.leaguePoints,
      stats.leagueTier, stats.level, stats.soloQueue.losses, stats.soloQueue.wins
    )
    bundle.putSerializable("data", data)
    val frag = new SummonerProfileFragment
    frag.setArguments(bundle)
    frag
  }

  private case class Data(name: String, kda: String, leagueDivision: String, leagueName: String, leaguePoints: String,
                          leagueTier: String, level: Int, lose: Int, win: Int)

}
