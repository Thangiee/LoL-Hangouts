package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Champion

import scala.util.Try

class SummonerTopChampFragment extends SFragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.summoner_top_champ, container, false)
    val champions = getArguments.getSerializable("champions-key").asInstanceOf[List[Try[Champion]]]
    find[TextView](R.id.output).setText(champions(0).get.toString)
    view
  }
}

object SummonerTopChampFragment {
  def newInstance(champions: List[Try[Champion]]): SummonerTopChampFragment = {
    val bundle = new Bundle()
    bundle.putSerializable("champions-key", champions)
    val frag = new SummonerTopChampFragment
    frag.setArguments(bundle)
    frag
  }
}
