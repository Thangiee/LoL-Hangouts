package com.thangiee.LoLHangouts.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view._
import android.view.inputmethod.InputMethodManager
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.MainActivity
import com.thangiee.LoLHangouts.utils.Events
import com.thangiee.LoLHangouts.utils.Events.SummonerCardClicked
import de.greenrobot.event.EventBus

class ChatScreenFragment extends TFragment with PanelSlideListener {
  private lazy val slidingLayout = find[SlidingPaneLayout](R.id.chat_sliding_pane)
  private lazy val imm = getActivity.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setRetainInstance(true)
    view = inflater.inflate(R.layout.chat_screen, container, false)

    slidingLayout.openPane()    // show the friend list fragment
    slidingLayout.setPanelSlideListener(this)
    slidingLayout.setSliderFadeColor(R.color.slider_fade.r2Color)
    slidingLayout.setShadowResource(R.drawable.sliding_pane_shadow)

    getFragmentManager.beginTransaction().add(R.id.chat_left_pane, new FriendListFragment).commit()
    getFragmentManager.beginTransaction().add(R.id.chat_content_pane, BlankFragment.newInstance(R.string.no_current_chat)).commit()

    view
  }

  override def onResume(): Unit = {
    super.onResume()
    EventBus.getDefault.register(this)
    appCtx.isFriendListOpen = slidingLayout.isOpen
    appCtx.isChatOpen = !slidingLayout.isOpen
    EventBus.getDefault.postSticky(new Events.ClearChatNotification)
  }

  override def onPause(): Unit = {
    super.onPause()
    EventBus.getDefault.unregister(this, classOf[SummonerCardClicked])
    appCtx.isChatOpen = false
    appCtx.isFriendListOpen = false
    appCtx.activeFriendChat = ""
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
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_previous) // chage AB home icon
    setHasOptionsMenu(true)
    imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
    appCtx.isFriendListOpen = false
    appCtx.isChatOpen = true
    EventBus.getDefault.postSticky(new Events.ClearChatNotification) // clear notification

    getFragmentManager.findFragmentById(R.id.chat_content_pane) match {
      case fragment: ChatPaneFragment ⇒
        fragment.setMessagesRead()
        fragment.setHasOptionsMenu(true) // change AB menu items
        getActivity.getActionBar.setTitle(appCtx.activeFriendChat) // set AB title to name of friend in chat with
      case _ ⇒
    }
  }

  override def onPanelOpened(panel: View): Unit = { // friend list pane open
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer) // change AB home icon
    getActivity.getActionBar.setTitle(R.string.app_name)  // change AB title to app name
    getFragmentManager.findFragmentById(R.id.chat_content_pane).setHasOptionsMenu(false)  // change AB menu items
    imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
    appCtx.isFriendListOpen = true
    appCtx.isChatOpen = false
    EventBus.getDefault.postSticky(new Events.ClearChatNotification)  // clear notification
    EventBus.getDefault.postSticky(new Events.ClearLoginNotification)  // clear notification
    EventBus.getDefault.postSticky(new Events.RefreshFriendList)  // refresh friend list
  }

  def onEvent(event: SummonerCardClicked): Unit = {
    info("[*]onEvent: "+event.summoner.name+" summoner card clicked")
    // don't re-initialize fragment if the opening chat pane is the same as the active one
    if (appCtx.activeFriendChat != event.summoner.name) {
      appCtx.activeFriendChat = event.summoner.name
      getFragmentManager.beginTransaction().replace(R.id.chat_content_pane, ChatPaneFragment.newInstance(event.summoner)).commit()
    }
    slidingLayout.closePane()
  }
}
