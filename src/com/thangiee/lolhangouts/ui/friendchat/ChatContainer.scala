package com.thangiee.lolhangouts.ui.friendchat

import android.content.Context
import android.support.v4.widget.SlidingPaneLayout
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener
import android.support.v7.app.ActionBar
import android.view._
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.{AdapterView, Spinner}
import com.balysv.materialmenu.MaterialMenuDrawable
import com.balysv.materialmenu.MaterialMenuDrawable.AnimationState
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.{GetUserUseCaseImpl, MarkMsgReadUseCaseImp, SetActiveChatUseCaseImpl}
import com.thangiee.lolhangouts.ui.core.Container
import com.thangiee.lolhangouts.ui.custom.MaterialSpinnerAdapter
import com.thangiee.lolhangouts.ui.utils.Events.{CreateFriendGroup, FriendCardClicked, UpdateFriendCard}
import com.thangiee.lolhangouts.ui.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatContainer(implicit ctx: Context) extends SlidingPaneLayout(ctx) with Container with PanelSlideListener {
  private lazy val friendListView = new FriendListView()
  private lazy val chatView       = new ChatView()

  private lazy val spinnerContainer   = layoutInflater.inflate(R.layout.toolbar_spinner, toolbar, false)
  private lazy val friendGroupAdapter = new MaterialSpinnerAdapter(Seq("All", "Online", "Offline"))

  private lazy val loadUser        = GetUserUseCaseImpl().loadUser()
  private val setActiveChatUseCase = SetActiveChatUseCaseImpl()
  private val markMsgReadUseCase   = MarkMsgReadUseCaseImp()

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    EventBus.getDefault.register(this)
    removeAllViews()
    addView(friendListView)
    addView(chatView)

    toolbar.setTitle(R.string.app_name)
    toolbar.setSubtitle(null)
    navIcon.setIconState(MaterialMenuDrawable.IconState.BURGER)

    // setup the spinner in the toolbar to filter friend list by groups
    val lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    toolbar.addView(spinnerContainer, lp)

    loadUser.onSuccess { case Good(user) => friendGroupAdapter.addItems(user.groupNames.filter(_ != "**Default")) }

    val spinner = spinnerContainer.find[Spinner](R.id.toolbar_spinner)
    spinner.setAdapter(friendGroupAdapter)
    spinner.setOnItemSelectedListener(new OnItemSelectedListener {
      def onNothingSelected(adapterView: AdapterView[_]): Unit = {}
      def onItemSelected(adapterView: AdapterView[_], view: View, position: Int, id: Long): Unit = {
        friendListView.friendGroupToShow = friendGroupAdapter.getItem(position)
        EventBus.getDefault.post(Events.ReloadFriendCardList())
      }
    })

    openPane() // show the friend list view
    setPanelSlideListener(this)
    setShadowDrawableLeft(R.drawable.sliding_pane_shadow)
    setCoveredFadeColor(R.color.md_grey_500.r2Color)

    appCtx.isFriendListOpen = isOpen
    appCtx.isChatOpen = !isOpen

    EventBus.getDefault.postSticky(Events.ClearChatNotification())
  }

  override def onDetachedFromWindow(): Unit = {
    EventBus.getDefault.unregister(this)
    appCtx.isChatOpen = false
    appCtx.isFriendListOpen = false
    toolbar.removeView(spinnerContainer)
    super.onDetachedFromWindow()
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menu.clear()
    if (appCtx.isChatOpen) menuInflater.inflate(R.menu.delete, menu)
    menuInflater.inflate(R.menu.overflow, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_delete => chatView.showDeleteMessageDialog(); true
      case _                => false
    }
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
    loadUser.onSuccess {
      case Good(user) => user.currentFriendChat.foreach { friendName =>
        info(s"[*] mark messages in chat between user and $friendName as read")
        markMsgReadUseCase.markAsRead(friendName)
        EventBus.getDefault.post(UpdateFriendCard(friendName))
      }
    }
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
    setActiveChatUseCase.setActiveChat(event.friend)
    closePane()

    Future {
      // wait til the friend list complete slide close before doing other things
      // to avoid laggy sliding animation
      while (appCtx.isFriendListOpen) {}

      runOnUiThread {
        chatView.setFriendIcon(event.friend.name, event.friend.regionId)
        chatView.setFriend(event.friend)
      }
    }
  }

  def onEvent(event: CreateFriendGroup): Unit ={
    friendGroupAdapter.addItem(event.groupName)
  }
}
