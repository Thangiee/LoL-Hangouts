package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.{View, ViewGroup, LayoutInflater}
import com.thangiee.LoLWithFriends.R

class NoChatPaneFragment extends Fragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    inflater.inflate(R.layout.no_current_chat, container, false)
  }
}
