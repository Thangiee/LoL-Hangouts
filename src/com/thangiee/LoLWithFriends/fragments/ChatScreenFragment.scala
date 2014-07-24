package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view.inputmethod.InputMethodManager
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.utils.Events.SummonerCardClicked
import de.greenrobot.event.EventBus

class ChatScreenFragment extends Fragment with PanelSlideListener {
  private var view: View = _
  private lazy val slidingLayout = view.findViewById(R.id.chat_sliding_pane).asInstanceOf[SlidingPaneLayout]
  private lazy val imm = getActivity.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.chat_screen, container, false)

    slidingLayout.openPane()
    slidingLayout.setPanelSlideListener(this)

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

  override def onPanelSlide(panel: View, slideOffset: Float): Unit = {}

  override def onPanelClosed(panel: View): Unit = imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard

  override def onPanelOpened(panel: View): Unit = imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
}
