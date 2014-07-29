package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view.inputmethod.InputMethodManager
import android.view._
import com.thangiee.LoLWithFriends.activities.MainActivity
import com.thangiee.LoLWithFriends.utils.Events
import com.thangiee.LoLWithFriends.utils.Events.SummonerCardClicked
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import org.scaloid.common._

class ChatScreenFragment extends Fragment with PanelSlideListener with TagUtil {
  private var view: View = _
  private lazy val slidingLayout = view.findViewById(R.id.chat_sliding_pane).asInstanceOf[SlidingPaneLayout]
  private lazy val imm = getActivity.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.chat_screen, container, false)

    slidingLayout.openPane()    // show the friend list fragment
    slidingLayout.setPanelSlideListener(this)
    slidingLayout.setParallaxDistance(250)
    slidingLayout.setSliderFadeColor(getResources.getColor(R.color.slider_fade))
    slidingLayout.setShadowResource(R.drawable.sliding_pane_shadow)

    getFragmentManager.beginTransaction().add(R.id.chat_left_pane, new FriendListFragment).commit()
    getFragmentManager.beginTransaction().add(R.id.chat_content_pane, new NoChatPaneFragment).commit()

    view
  }

  override def onResume(): Unit = {
    super.onResume()
    EventBus.getDefault.register(this)
    MyApp.isFriendListOpen = slidingLayout.isOpen
    MyApp.isChatOpen = !slidingLayout.isOpen
  }

  override def onPause(): Unit = {
    super.onPause()
    EventBus.getDefault.unregister(this, classOf[SummonerCardClicked])
    MyApp.isChatOpen = false
    MyApp.isFriendListOpen = false
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => slidingLayout.openPane(); return true
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  override def onPanelSlide(panel: View, slideOffset: Float): Unit = {}

  override def onPanelClosed(panel: View): Unit = { // chat pane open
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_previous)
    setHasOptionsMenu(true)
    imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
    MyApp.isFriendListOpen = false
    MyApp.isChatOpen = true

    if (!MyApp.activeFriendChat.isEmpty) {
      getFragmentManager.findFragmentById(R.id.chat_content_pane).asInstanceOf[ChatPaneFragment].setMessagesRead()
      getFragmentManager.findFragmentById(R.id.chat_content_pane).setHasOptionsMenu(true)
    }
  }

  override def onPanelOpened(panel: View): Unit = { // friend list pane open
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    getFragmentManager.findFragmentById(R.id.chat_content_pane).setHasOptionsMenu(false)
    imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
    MyApp.isFriendListOpen = true
    MyApp.isChatOpen = false
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)
  }

  def onEvent(event: SummonerCardClicked): Unit = {
    info("[*]onEvent: "+event.summoner.name+" summoner card clicked")
    // don't re-initialize fragment if the opening chat pane is the same as the active one
    if (MyApp.activeFriendChat != event.summoner.name) {
      MyApp.activeFriendChat = event.summoner.name
      getFragmentManager.beginTransaction().replace(R.id.chat_content_pane, ChatPaneFragment.newInstance(event.summoner)).commit()
    }
    slidingLayout.closePane()
  }
}
