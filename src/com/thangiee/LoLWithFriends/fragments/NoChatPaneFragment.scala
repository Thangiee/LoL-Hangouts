package com.thangiee.LoLWithFriends.fragments

import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R

class NoChatPaneFragment extends SFragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    inflater.inflate(R.layout.no_current_chat, container, false)
  }
}
