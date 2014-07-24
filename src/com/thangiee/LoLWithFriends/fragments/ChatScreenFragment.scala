package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.utils.Events.SummonerCardClicked
import de.greenrobot.event.EventBus

class ChatScreenFragment extends Fragment {
  private var view: View = _
  private lazy val slidingLayout = view.findViewById(R.id.chat_sliding_pane).asInstanceOf[SlidingPaneLayout]

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.chat_screen, container, false)

    slidingLayout.openPane()

    getFragmentManager.beginTransaction().add(R.id.chat_left_pane, new FriendListFragment).commit()
    getFragmentManager.beginTransaction().add(R.id.chat_content_pane, new NoChatPaneFragment).commit()

    view
  }

  override def onResume(): Unit = {
    super.onResume()
    EventBus.getDefault.register(this)
  }


  override def onPause(): Unit = {
    super.onPause()
    EventBus.getDefault.unregister(this, null)
  }

  def onEvent(event: SummonerCardClicked): Unit = {
    getFragmentManager.beginTransaction().replace(R.id.chat_content_pane, ChatPaneFragment.newInstance(event.summoner)).commit()
    slidingLayout.closePane()
  }
}
