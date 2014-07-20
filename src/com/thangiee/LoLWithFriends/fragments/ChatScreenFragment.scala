package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R

class ChatScreenFragment extends Fragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    val view = inflater.inflate(R.layout.chat_screen, container, false)

    val slidingLayout = view.findViewById(R.id.chat_sliding_pane).asInstanceOf[SlidingPaneLayout]
    slidingLayout.openPane()

    getFragmentManager.beginTransaction().add(R.id.chat_left_pane, new FriendListFragment).commit()
    getFragmentManager.beginTransaction().add(R.id.chat_content_pane, new ChatPaneFragment).commit()

    view
  }
}
