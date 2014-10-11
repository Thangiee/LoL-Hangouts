package com.thangiee.LoLHangouts.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{ViewLiveGameStatsActivity, ViewOtherSummonerActivity}

case class BlankFragment() extends TFragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.blank_screen, container, false)
    val stringResId = getArguments.getInt("msg-key")
    val msg = if (stringResId == 0) "" else stringResId.r2String
    find[TextView](R.id.tv_blank_screen_msg).setText(msg)

    view
  }
}

object BlankFragment {
  def apply(messageResId: Int = 0): BlankFragment = {
    BlankFragment().args("msg-key" → messageResId)
  }

  def withSummonerSearch(): BlankFragment = {
    val frag = new BlankFragment with SummonerSearch {
      override val defaultSearchText: String = ""

      override def onSearchCompleted(searchedQuery: String, region: String): Unit = {
        startActivity(ViewOtherSummonerActivity(searchedQuery, region))
      }
    }
    frag.args("msg-key" → R.string.summoner_search_screen_msg)
  }

  def withLiveGameSearch(currentUser: String): BlankFragment = {
    val frag = new BlankFragment with SummonerSearch {
      override val defaultSearchText: String = currentUser

      override def onSearchCompleted(searchedQuery: String, region: String): Unit = {
        startActivity(ViewLiveGameStatsActivity(searchedQuery, region))
      }
    }
    frag.args("msg-key" → R.string.live_game_screen_msg)
  }
}
