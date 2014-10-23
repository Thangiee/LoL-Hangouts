package com.thangiee.LoLHangouts.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view._
import android.view.inputmethod.InputMethodManager
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.MainActivity
import com.thangiee.LoLHangouts.utils.Events.FriendCardClicked
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus

import scala.collection.JavaConversions._

case class ChatScreenFragment() extends TFragment with PanelSlideListener {
  private lazy val imm           = getActivity.getSystemService(Context.INPUT_METHOD_SERVICE).asInstanceOf[InputMethodManager]
  lazy         val slidingLayout = find[SlidingPaneLayout](R.id.chat_sliding_pane)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setRetainInstance(true)
    view = inflater.inflate(R.layout.chat_screen, container, false)

    slidingLayout.openPane() // show the friend list fragment
    slidingLayout.setPanelSlideListener(this)
    slidingLayout.setSliderFadeColor(R.color.slider_fade.r2Color)
    slidingLayout.setShadowResource(R.drawable.sliding_pane_shadow)

    getFragmentManager.beginTransaction().replace(R.id.chat_left_pane, FriendListFragment()).commit()
    getFragmentManager.beginTransaction().replace(R.id.chat_content_pane, BlankFragment(R.string.no_current_chat)).commit()

    view
  }

  override def onResume(): Unit = {
    super.onResume()
    EventBus.getDefault.register(this)
    appCtx.isFriendListOpen = slidingLayout.isOpen
    appCtx.isChatOpen = !slidingLayout.isOpen

    if (appCtx.isChatOpen) // if resume and the chat is open, set read for messages in that chat
      DB.getUnreadMessages(appCtx.currentUser, appCtx.activeFriendChat).map(m ⇒ m.setRead(true).save())

    EventBus.getDefault.postSticky(Events.ClearChatNotification())
  }

  override def onPause(): Unit = {
    super.onPause()
    EventBus.getDefault.unregister(this)
    appCtx.isChatOpen = false
    appCtx.isFriendListOpen = false
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
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification

    getFragmentManager.findFragmentById(R.id.chat_content_pane) match {
      case fragment: ChatPaneFragment ⇒
        fragment.setMessagesRead()
        fragment.setHasOptionsMenu(true) // change AB menu items
      case _ ⇒
    }
  }

  override def onPanelOpened(panel: View): Unit = { // friend list pane open
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer) // change AB home icon
    getActivity.getActionBar.setTitle(R.string.app_name) // change AB title to app name
    getFragmentManager.findFragmentById(R.id.chat_content_pane).setHasOptionsMenu(false) // change AB menu items
    imm.hideSoftInputFromWindow(panel.getWindowToken, 0) // hide keyboard
    appCtx.isFriendListOpen = true
    appCtx.isChatOpen = false
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification
    EventBus.getDefault.postSticky(Events.ClearLoginNotification()) // clear notification
    EventBus.getDefault.postSticky(Events.RefreshFriendList()) // refresh friend list
  }

  def onEvent(event: FriendCardClicked): Unit = {
    info(s"[*]onEvent: ${event.friend.name} friend card clicked")
    val fragment = ChatPaneFragment(event.friend.name)
    getFragmentManager.beginTransaction().replace(R.id.chat_content_pane, fragment).commit()
    slidingLayout.closePane()
  }
}
