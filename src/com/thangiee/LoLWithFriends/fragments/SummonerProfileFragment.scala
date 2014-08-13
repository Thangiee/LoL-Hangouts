package com.thangiee.LoLWithFriends.fragments

import java.text.DecimalFormat

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, TextView}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLStatistics
import com.thangiee.LoLWithFriends.fragments.SummonerProfileFragment.Data
import com.thangiee.LoLWithFriends.utils.SummonerUtils

class SummonerProfileFragment extends SFragment {

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
    find[TextView](R.id.tv_profile_lost).setText(data.lose.toString + " L")
    find[TextView](R.id.tv_profile_win).setText(data.win.toString + " W")
    SummonerUtils.loadIconInto(getActivity, data.name, find[ImageView](R.id.img_profile_icon))

    val rateTextView = find[TextView](R.id.tv_profile_rate)
    rateTextView.setText(new DecimalFormat("###.##").format(percent) + "%")
    rateTextView.setTextColor(if (percent >= 50) android.R.color.holo_green_dark.r2Color else R.color.red.r2Color)

    val badgeImageView = find[ImageView](R.id.img_profile_badge)
    data.leagueTier.toUpperCase match {
      case "BRONZE"   ⇒ badgeImageView.setImageResource(R.drawable.badge_bronze)
      case "SILVER"   ⇒ badgeImageView.setImageResource(R.drawable.badge_silver)
      case "GOLD"     ⇒ badgeImageView.setImageResource(R.drawable.badge_gold)
      case "PLATINUM" ⇒ badgeImageView.setImageResource(R.drawable.badge_platinum)
      case "CHALLENGER" ⇒ badgeImageView.setImageResource(R.drawable.badge_challenger)
      case _          ⇒ badgeImageView.setImageResource(R.drawable.badge_unranked)
    }

    view
  }
}

object SummonerProfileFragment {
  def newInstance(summonerName: String, stats: LoLStatistics): SummonerProfileFragment = {
    val bundle = new Bundle()
    val data = Data(
      summonerName, stats.kda(), stats.leagueDivision(), stats.leagueName(), stats.leaguePoints(),
      stats.leagueTier(), stats.level(), stats.lose(), stats.win()
    )
    bundle.putSerializable("data", data)
    val frag = new SummonerProfileFragment
    frag.setArguments(bundle)
    frag
  }

  private case class Data(name: String, kda: String, leagueDivision: String, leagueName: String, leaguePoints: String,
                          leagueTier: String, level: Int, lose: Int, win: Int)

}
