package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view.{Menu, MenuInflater, View}
import com.balysv.materialmenu.MaterialMenuDrawable
import com.balysv.materialmenu.MaterialMenuDrawable.AnimationState
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.interactor.{GetUserUseCaseImpl, SetActiveChatUseCaseImpl}
import com.thangiee.LoLHangouts.utils.Events.{FriendCardClicked, UpdateFriendCard}
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{Container, R}
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatContainer(implicit ctx: Context) extends SlidingPaneLayout(ctx) with Container with PanelSlideListener {
  lazy val friendListView = new FriendListView()
  lazy val chatView       = new ChatView()

  val getUserUseCase       = GetUserUseCaseImpl()
  val setActiveChatUseCase = SetActiveChatUseCaseImpl()

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    EventBus.getDefault.register(this)
    removeAllViews()
    addView(friendListView)
    addView(chatView)

    navIcon.setIconState(MaterialMenuDrawable.IconState.BURGER)

    getUserUseCase.loadUser().map { user =>
      info("[*] loading user info to use in load user summoner icon")
      val icon = SummonerUtils.getProfileIcon(user.inGameName, user.region.id, 55)
      runOnUiThread(chatView.setUserIcon(icon))
    }

    openPane() // show the friend list
    setPanelSlideListener(this)

    appCtx.isFriendListOpen = isOpen
    appCtx.isChatOpen = !isOpen

    EventBus.getDefault.postSticky(Events.ClearChatNotification())
  }

  override def onDetachedFromWindow(): Unit = {
    super.onDetachedFromWindow()
    removeAllViews()
    EventBus.getDefault.unregister(this)
    appCtx.isChatOpen = false
    appCtx.isFriendListOpen = false
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menu.clear()
    if (appCtx.isChatOpen) menuInflater.inflate(R.menu.delete, menu)
    menuInflater.inflate(R.menu.overflow, menu)
    true
  }

  override def onNavIconClick(): Boolean = {
    if (appCtx.isChatOpen) {
      openPane()
      return true
    }
    false
  }

  override def onBackPressed(): Boolean = {
    if (appCtx.isChatOpen) {
      openPane() // close the chat and open the friend list
      return true
    }
    false
  }

  override def getView: View = this

  override def onPanelSlide(view: View, v: Float): Unit = {
    navIcon.setTransformationOffset(AnimationState.BURGER_ARROW, 1 - v)
  }

  override def onPanelClosed(view: View): Unit = {
    invalidateOptionsMenu()
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    if (toolbar.getTitle == R.string.app_name.r2String) toolbar.setTitle(chatView.getFriend.map(_.name).getOrElse("NOBODY"))
    appCtx.isFriendListOpen = false
    appCtx.isChatOpen = true
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification
  }

  override def onPanelOpened(view: View): Unit = {
    invalidateOptionsMenu()
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    toolbar.setTitle(R.string.app_name)
    appCtx.isFriendListOpen = true
    appCtx.isChatOpen = false
    EventBus.getDefault.postSticky(Events.ClearChatNotification()) // clear notification
    EventBus.getDefault.postSticky(Events.ClearLoginNotification()) // clear notification
  }

  def onEvent(event: FriendCardClicked): Unit = {
    info(s"[*] onEvent: ${event.friend.name} friend card clicked")
    toolbar.setTitle(event.friend.name)
    chatView.clearMessages()
    closePane()

    Future {
      // wait til the friend list complete slide close before doing other things
      // to avoid laggy sliding animation
      while (appCtx.isFriendListOpen) {}

      val icon = SummonerUtils.getProfileIcon(event.friend.name, event.friend.regionId, 55)
      runOnUiThread {
        setActiveChatUseCase.setActiveChat(event.friend)
        chatView.setFriendIcon(icon)
        chatView.setFriend(event.friend)
        EventBus.getDefault.post(UpdateFriendCard(event.friend))
      }
    }
  }
}
