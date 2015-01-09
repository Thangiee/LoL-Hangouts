package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view.View
import com.thangiee.LoLHangouts.Container
import com.thangiee.LoLHangouts.utils.Events.FriendCardClicked
import com.thangiee.LoLHangouts.domain.utils.Logger._
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus

class ChatContainer(implicit ctx: Context) extends SlidingPaneLayout(ctx) with Container with PanelSlideListener {
  lazy val friendListView = new FriendListView()
  lazy val chatView       = new ChatView()

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    EventBus.getDefault.register(this)
    addView(friendListView)
    addView(chatView)

    openPane() // show the friend list
    setPanelSlideListener(this)

    appCtx.isFriendListOpen = isOpen
    appCtx.isChatOpen = !isOpen

    EventBus.getDefault.postSticky(Events.ClearChatNotification())
  }

  override def onDetachedFromWindow(): Unit = {
    super.onDetachedFromWindow()
    EventBus.getDefault.unregister(this)
    appCtx.isChatOpen = false
    appCtx.isFriendListOpen = false
  }

  override def onBackPressed(): Boolean = {
    if (appCtx.isChatOpen) {
      openPane() // close the chat and open the friend list
      return true
    }

    false
  }

  override def getView: View = this

  override def onPanelSlide(view: View, v: Float): Unit = {}

  override def onPanelClosed(view: View): Unit = {
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    appCtx.isFriendListOpen = false
    appCtx.isChatOpen = true
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification
  }

  override def onPanelOpened(view: View): Unit = {
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    appCtx.isFriendListOpen = true
    appCtx.isChatOpen = false
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification
    EventBus.getDefault.postSticky(Events.ClearLoginNotification()) // clear notification
    EventBus.getDefault.postSticky(Events.ReloadFriendCardList()) // refresh friend list
  }

  def onEvent(event: FriendCardClicked): Unit = {
    info(s"[*] onEvent: ${event.friend.name} friend card clicked")
    chatView.setFriend(event.friend)
    closePane()
  }
}
