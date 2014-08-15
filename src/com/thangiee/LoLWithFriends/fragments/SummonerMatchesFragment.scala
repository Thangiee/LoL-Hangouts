package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.squareup.picasso.Picasso
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Match

import scala.collection.JavaConversions._

class SummonerMatchesFragment extends SFragment {
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
    val matches = getArguments.getSerializable("matches-key").asInstanceOf[List[Match]]
    val matchDictionary = new BindDictionary[Match]()

    matchDictionary.addStaticImageField(R.id.img_match_champ, new StaticImageLoader[Match] {
      override def loadImage(m: Match, image: ImageView, p3: Int): Unit = {
        Picasso.`with`(ctx).load("http://www.mobafire.com/images/champion/icon/" + m.champName.toLowerCase + ".png")
          .placeholder(R.drawable.load_error)
          .error(R.drawable.load_error)
          .into(image)
      }
    })

    matchDictionary.addStringField(R.id.tv_match_type, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.queueType
    })

    matchDictionary.addStringField(R.id.tv_match_outcome, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.outCome
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if(m.outCome.toLowerCase.equals("win")) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_date, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.date
    })

    matchDictionary.addStringField(R.id.tv_match_len, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.duration
    })

    matchDictionary.addStringField(R.id.tv_match_perf, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.performance + "%"
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.performance >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_avg_k, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgStats.kills.toString
    })
    matchDictionary.addStringField(R.id.tv_match_avg_more_k, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.kills.toString
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.kills >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_avg_d, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgStats.deaths.toString
    })
    matchDictionary.addStringField(R.id.tv_match_avg_more_d, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.deaths.toString
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.deaths <= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_avg_a, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgStats.assists.toString
    })
    matchDictionary.addStringField(R.id.tv_match_avg_more_a, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.assists.toString
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.assists >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_avg_cs, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgStats.cs.toString
    })
    matchDictionary.addStringField(R.id.tv_match_avg_more_cs, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.cs.toString
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.cs >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    matchDictionary.addStringField(R.id.tv_match_avg_g, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgStats.gold.toString
    })
    matchDictionary.addStringField(R.id.tv_match_avg_more_g, new StringExtractor[Match] {
      override def getStringValue(m: Match, pos: Int): String = m.avgBetterStats.gold.toString
    }).conditionalTextColor(new BooleanExtractor[Match] {
      override def getBooleanValue(m: Match, pos: Int): Boolean = if (m.avgBetterStats.gold >= 0) true else false
    }, R.color.green.r2Color, R.color.red.r2Color)

    val adapter = new FunDapter[Match](ctx, matches, R.layout.match_history, matchDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    matchListView.setAdapter(adapter)
  }
}

object SummonerMatchesFragment {
  def newInstance(matches: List[Match]): SummonerMatchesFragment = {
    val bundle = new Bundle()
    bundle.putSerializable("matches-key", matches)
    val frag = new SummonerMatchesFragment
    frag.setArguments(bundle)
    frag
  }
}
