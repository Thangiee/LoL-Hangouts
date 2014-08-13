package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.squareup.picasso.Picasso
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Champion

import scala.collection.JavaConversions._

class SummonerTopChampFragment extends SFragment {
  private lazy val champListView = find[ListView](R.id.listView)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.summoner_top_champ, container, false)
    populateList()
    view
  }

  private def populateList(): Unit = {
    val champions = getArguments.getSerializable("champions-key").asInstanceOf[List[Champion]]
    val champDictionary = new BindDictionary[Champion]()

    champDictionary.addStaticImageField(R.id.img_champ_icon, new StaticImageLoader[Champion] {
      override def loadImage(champ: Champion, image: ImageView, p3: Int): Unit = {
        Picasso.`with`(ctx).load(champ.iconUrl)
          .placeholder(R.drawable.load_error)
          .error(R.drawable.load_error)
          .into(image)
      }
    })
    champDictionary.addStringField(R.id.tv_champ_name, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.name
    })

    champDictionary.addStringField(R.id.tv_champ_perf, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.performance + "%"
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.performance >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    champDictionary.addStringField(R.id.tv_champ_game, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.numOfGame.toString
    })

    champDictionary.addStringField(R.id.tv_champ_avg_k, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgStats.kills.toString
    })
    champDictionary.addStringField(R.id.tv_champ_avg_more_k, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.kills.toString
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.kills >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    champDictionary.addStringField(R.id.tv_champ_avg_d, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgStats.deaths.toString
    })
    champDictionary.addStringField(R.id.tv_champ_avg_more_d, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.deaths.toString
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.deaths <= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    champDictionary.addStringField(R.id.tv_champ_avg_a, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgStats.assists.toString
    })
    champDictionary.addStringField(R.id.tv_champ_avg_more_a, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.assists.toString
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.assists >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    champDictionary.addStringField(R.id.tv_champ_avg_cs, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgStats.cs.toString
    })
    champDictionary.addStringField(R.id.tv_champ_avg_more_cs, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.cs.toString
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.cs >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    champDictionary.addStringField(R.id.tv_champ_avg_g, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgStats.gold.toString
    })
    champDictionary.addStringField(R.id.tv_champ_avg_more_g, new StringExtractor[Champion] {
      override def getStringValue(champ: Champion, pos: Int): String = champ.avgBetterStats.gold.toString
    }).conditionalTextColor(new BooleanExtractor[Champion] {
      override def getBooleanValue(champ: Champion, pos: Int): Boolean = if (champ.avgBetterStats.gold >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    val adapter = new FunDapter[Champion](ctx, champions, R.layout.champion_stats, champDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    champListView.setAdapter(adapter)
  }
}

object SummonerTopChampFragment {
  def newInstance(champions: List[Champion]): SummonerTopChampFragment = {
    val bundle = new Bundle()
    bundle.putSerializable("champions-key", champions)
    val frag = new SummonerTopChampFragment
    frag.setArguments(bundle)
    frag
  }
}
