package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R

class ChatPaneFragment extends Fragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    inflater.inflate(R.layout.chat_pane, container, false)
  }
}
