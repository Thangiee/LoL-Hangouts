package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R

class BlankFragment extends SFragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.blank_screen, container, false)
    val msg = getArguments.getString("msg-key")
    find[TextView](R.id.tv_blank_screen_msg).setText(msg)

    view
  }
}

object BlankFragment {
  def newInstance(message: String = ""): BlankFragment = {
    val bundle = new Bundle()
    bundle.putString("msg-key", message)
    val frag = new BlankFragment
    frag.setArguments(bundle)
    frag
  }

  def newInstanceWithSummonerSearch(message: String = ""): BlankFragment = {
    val bundle = new Bundle()
    bundle.putString("msg-key", message)
    val frag = new BlankFragment with SummonerSearch
    frag.setArguments(bundle)
    frag
  }
}
