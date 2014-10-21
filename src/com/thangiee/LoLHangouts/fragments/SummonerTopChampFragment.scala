package com.thangiee.LoLHangouts.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.Champion
import com.thangiee.LoLHangouts.utils.ChampIconAssetFile
import com.thangiee.common._

import scala.collection.JavaConversions._

case class SummonerTopChampFragment() extends TFragment {
  private lazy val champListView = find[ListView](R.id.listView)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.summoner_top_champ, container, false)
    view
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    populateList()
  }

  private def populateList(): Unit = {
    val green = R.color.green.r2Color
    val red   = R.color.red.r2Color
    val champions = getArguments.getSerializable("champions-key").asInstanceOf[List[Champion]]
    val champDictionary = new BindDictionary[Champion]()

    champDictionary.addStaticImageField(R.id.img_champ_icon, new StaticImageLoader[Champion] {
      override def loadImage(champ: Champion, image: ImageView, p3: Int): Unit =
        image.setImageDrawable(ChampIconAssetFile(champ.name).toDrawable)
    })

    champDictionary.addStringField(R.id.tv_champ_name, (champ: Champion) ⇒ champ.name)

    champDictionary.addStringField(R.id.tv_champ_perf, (champ: Champion) ⇒ champ.avgBetterStats.performance + "%")
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.performance >= 0, green, red)

    champDictionary.addStringField(R.id.tv_champ_game, (champ: Champion) ⇒ champ.numOfGame.toString)

    champDictionary.addStringField(R.id.tv_champ_avg_k, (champ: Champion) ⇒ champ.avgStats.kills.toString)
    champDictionary.addStringField(R.id.tv_champ_avg_more_k, (champ: Champion) ⇒ champ.avgBetterStats.kills.toString)
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.kills >= 0, green, red)

    champDictionary.addStringField(R.id.tv_champ_avg_d, (champ: Champion) ⇒ champ.avgStats.deaths.toString)
    champDictionary.addStringField(R.id.tv_champ_avg_more_d, (champ: Champion) ⇒ champ.avgBetterStats.deaths.toString)
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.deaths <= 0, green, red)

    champDictionary.addStringField(R.id.tv_champ_avg_a, (champ: Champion) ⇒ champ.avgStats.assists.toString)
    champDictionary.addStringField(R.id.tv_champ_avg_more_a, (champ: Champion) ⇒ champ.avgBetterStats.assists.toString)
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.assists >= 0, green, red)

    champDictionary.addStringField(R.id.tv_champ_avg_cs, (champ: Champion) ⇒ champ.avgStats.cs.toString)
    champDictionary.addStringField(R.id.tv_champ_avg_more_cs, (champ: Champion) ⇒ champ.avgBetterStats.cs.toString)
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.cs >= 0, green, red)

    champDictionary.addStringField(R.id.tv_champ_avg_g, (champ: Champion) ⇒ champ.avgStats.gold.toString)
    champDictionary.addStringField(R.id.tv_champ_avg_more_g, (champ: Champion) ⇒ champ.avgBetterStats.gold.toString)
                   .conditionalTextColor((champ: Champion) ⇒ champ.avgBetterStats.gold >= 0, green, red)

    val adapter = new FunDapter[Champion](ctx, champions, R.layout.champion_stats, champDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    champListView.setAdapter(adapter)
  }
}

object SummonerTopChampFragment {
  def apply(champions: List[Champion]): SummonerTopChampFragment = {
    SummonerTopChampFragment().args("champions-key" → champions)
  }
}
