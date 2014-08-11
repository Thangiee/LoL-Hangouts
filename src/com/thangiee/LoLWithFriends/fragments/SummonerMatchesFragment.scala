package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Match

import scala.util.Try

class SummonerMatchesFragment extends SFragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.summoner_matches, container, false)
    val matches = getArguments.getSerializable("matches-key").asInstanceOf[List[Try[Match]]]
    find[TextView](R.id.output).setText(matches(1).get.toString)
    view
  }
}

object SummonerMatchesFragment {
  def newInstance(matches: List[Try[Match]]): SummonerMatchesFragment = {
    val bundle = new Bundle()
    bundle.putSerializable("matches-key", matches)
    val frag = new SummonerMatchesFragment
    frag.setArguments(bundle)
    frag
  }
}