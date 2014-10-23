package com.thangiee.LoLHangouts.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.Match
import com.thangiee.LoLHangouts.utils.ChampIconAssetFile
import com.thangiee.LoLHangouts.utils._

import scala.collection.JavaConversions._

case class SummonerMatchesFragment() extends TFragment {
  private lazy val matchListView = find[ListView](R.id.listView)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.summoner_matches, container, false)
    view
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    populateList()
  }

  private def populateList(): Unit = {
    val green = R.color.green.r2Color
    val red   = R.color.red.r2Color
    val matches = getArguments.getSerializable("matches-key").asInstanceOf[List[Match]]
    val matchDictionary = new BindDictionary[Match]()

    matchDictionary.addStaticImageField(R.id.img_match_champ, new StaticImageLoader[Match] {
      override def loadImage(m: Match, image: ImageView, p3: Int): Unit =
        image.setImageDrawable(ChampIconAssetFile(m.champName).toDrawable)
    })

    matchDictionary.addStringField(R.id.tv_match_type, (m: Match) ⇒ m.queueType)

    matchDictionary.addStringField(R.id.tv_match_outcome, (m: Match) ⇒ m.outCome)
                   .conditionalTextColor((m: Match) ⇒ m.outCome.toLowerCase.equals("win"), green, red)

    matchDictionary.addStringField(R.id.tv_match_date, (m: Match) ⇒ m.date)

    matchDictionary.addStringField(R.id.tv_match_len, (m: Match) ⇒ m.duration.replace("≈", ""))

    matchDictionary.addStringField(R.id.tv_match_perf, (m: Match) ⇒ m.avgBetterStats.performance + "%")
                   .conditionalTextColor((m: Match) ⇒ m.avgBetterStats.performance >= 0, green, red)

    matchDictionary.addStringField(R.id.tv_match_avg_k, (m: Match) ⇒  m.avgStats.kills.toString)
    matchDictionary.addStringField(R.id.tv_match_avg_more_k, (m: Match) ⇒ m.avgBetterStats.kills.toString)
                   .conditionalTextColor((m: Match) ⇒  m.avgBetterStats.kills >= 0, green, red)

    matchDictionary.addStringField(R.id.tv_match_avg_d, (m: Match) ⇒ m.avgStats.deaths.toString)
    matchDictionary.addStringField(R.id.tv_match_avg_more_d, (m: Match) ⇒ m.avgBetterStats.deaths.toString)
                   .conditionalTextColor((m: Match) ⇒ m.avgBetterStats.deaths <= 0, green, red)

    matchDictionary.addStringField(R.id.tv_match_avg_a, (m: Match) ⇒ m.avgStats.assists.toString)
    matchDictionary.addStringField(R.id.tv_match_avg_more_a, (m: Match) ⇒ m.avgBetterStats.assists.toString)
                   .conditionalTextColor((m: Match) ⇒ m.avgBetterStats.assists >= 0, green, red)

    matchDictionary.addStringField(R.id.tv_match_avg_cs, (m: Match) ⇒ m.avgStats.cs.toString)
    matchDictionary.addStringField(R.id.tv_match_avg_more_cs, (m: Match) ⇒ m.avgBetterStats.cs.toString)
                   .conditionalTextColor((m: Match) ⇒ m.avgBetterStats.cs >= 0, green, red)

    matchDictionary.addStringField(R.id.tv_match_avg_g, (m: Match) ⇒ m.avgStats.gold.toString)
    matchDictionary.addStringField(R.id.tv_match_avg_more_g, (m: Match) ⇒ m.avgBetterStats.gold.toString)
                   .conditionalTextColor((m: Match) ⇒ m.avgBetterStats.gold >= 0, green, red)

    val adapter = new FunDapter[Match](ctx, matches, R.layout.match_history, matchDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    matchListView.setAdapter(adapter)
  }
}

object SummonerMatchesFragment {
  def apply(matches: List[Match]): SummonerMatchesFragment = {
    SummonerMatchesFragment().args("matches-key" → matches)
  }
}
