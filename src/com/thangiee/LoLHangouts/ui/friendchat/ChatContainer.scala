package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.view.{MenuInflater, Menu, View}
import com.balysv.materialmenu.MaterialMenuDrawable.AnimationState
import com.thangiee.LoLHangouts.{R, Container}
import com.thangiee.LoLHangouts.data.repository.UserRepoImpl
import com.thangiee.LoLHangouts.domain.interactor.GetUserUseCaseImpl
import com.thangiee.LoLHangouts.utils.Events.FriendCardClicked
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class ChatContainer(implicit ctx: Context) extends SlidingPaneLayout(ctx) with Container with PanelSlideListener {
  lazy val friendListView = new FriendListView()
  lazy val chatView       = new ChatView()

  implicit val userRepo = new UserRepoImpl()
  val getUserUseCase = new GetUserUseCaseImpl()

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    EventBus.getDefault.register(this)
    removeAllViews()
    addView(friendListView)
    addView(chatView)

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
    materialMenu.setTransformationOffset(AnimationState.BURGER_ARROW, 1 - v)
  }

  override def onPanelClosed(view: View): Unit = {
    invalidateOptionsMenu()
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    toolbar.setTitle(chatView.getFriend.map(_.name).getOrElse("NOBODY"))
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
    EventBus.getDefault.postSticky(Events.ReloadFriendCardList()) // refresh friend list
  }

  def onEvent(event: FriendCardClicked): Unit = {
    info(s"[*] onEvent: ${event.friend.name} friend card clicked")

    getUserUseCase.loadUser().map { user =>
      info("[*] loading user info to use in load friend summoner icon")
      val icon = SummonerUtils.getProfileIcon(event.friend.name, user.region.id, 55)
      runOnUiThread(chatView.setFriendIcon(icon))
    }

    chatView.setFriend(event.friend)
    closePane()
  }
}
