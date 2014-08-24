package com.thangiee.LoLHangouts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{ViewLiveGameStatsActivity, ViewOtherSummonerActivity}

class BlankFragment extends TFragment {
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
  def newInstance(messageResId: Int = 0): BlankFragment = {
    val bundle = new Bundle()
    bundle.putInt("msg-key", messageResId)
    val frag = new BlankFragment
    frag.setArguments(bundle)
    frag
  }

  def withSummonerSearch(): BlankFragment = {
    val bundle = new Bundle()
    bundle.putInt("msg-key", R.string.summoner_search_screen_msg)

    val frag = new BlankFragment with SummonerSearch {
      override val defaultSearchText: String = ""

      override def onSearchCompleted(searchedQuery: String, region: String): Unit = {
        val i = new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", searchedQuery).putExtra("region-key", region)
        startActivity(i)
      }
    }

    frag.setArguments(bundle)
    frag
  }

  def withLiveGameSearch(currentUser: String): BlankFragment = {
    val bundle = new Bundle()
    bundle.putInt("msg-key", R.string.live_game_screen_msg)

    val frag = new BlankFragment with SummonerSearch {
      override val defaultSearchText: String = currentUser

      override def onSearchCompleted(searchedQuery: String, region: String): Unit = {
        val i = new Intent(ctx, classOf[ViewLiveGameStatsActivity]).putExtra("name-key", searchedQuery).putExtra("region-key", region)
        startActivity(i)
      }
    }

    frag.setArguments(bundle)
    frag
  }
}
