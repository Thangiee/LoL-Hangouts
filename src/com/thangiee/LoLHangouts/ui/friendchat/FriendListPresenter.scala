package com.thangiee.LoLHangouts.ui.friendchat

import android.os.Handler
import com.github.nscala_time.time.Imports._
import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.interactor.GetFriendsUseCase
import com.thangiee.LoLHangouts.utils.Events.{ReloadFriendCardList, UpdateFriendCard, UpdateOnlineFriendsCard}
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class FriendListPresenter(view: FriendListView, getFriendsUseCase: GetFriendsUseCase) extends Presenter {
  var lock        = false
  val autoUpdateHandler = new Handler()
  val autoUpdateTask = new Runnable {
    override def run(): Unit = {
      info("[*] Auto updating online friend cards")
      EventBus.getDefault.post(UpdateOnlineFriendsCard())
      handler.postDelayed(this, 1.minutes.millis)
    }
  }

  override def initialize(): Unit = {
    super.initialize()
    EventBus.getDefault.register(this)
  }

  override def resume(): Unit = {
    super.resume()
    handler.postDelayed(autoUpdateTask, 1.minutes.millis)

    view.showLoading()
    loadCardList()
    view.hideLoading()
  }

  override def pause(): Unit = {
    super.pause()
    handler.removeCallbacksAndMessages(null)
  }

  override def shutdown(): Unit = {
    super.shutdown()
    EventBus.getDefault.unregister(this)
  }

  private def loadCardList(): Unit = {
    // lock use to prevent multiple calls to load list while it is already loading
    if (!lock) {
      lock = true
      info("[*] loading all friends")
      val (onFriends, offFriends) = getFriendsUseCase.loadFriendList().partition(_.isOnline)
      runOnUiThread(view.initCardList(onFriends, offFriends))
      lock = false
    }
    else info("[-] repopulate friend card list blocked")
  }

  def onEvent(event: ReloadFriendCardList): Unit = {
    info("[*] onEvent: request to reload friend list")
    loadCardList()
  }

  def onEvent(event: UpdateFriendCard): Unit = {
    info("[*] onEvent: request to update " + event.friendName + "friend card")

    // block RefreshFriendCard event when friend list is currently loading 
    if (!lock)
      getFriendsUseCase.loadFriendByName(event.friendName).map(f => runOnUiThread(view.updateCardContent(f)))
    else
      info("[-] Refresh friend card blocked")
  }
  
  def onEvent(events: UpdateOnlineFriendsCard): Unit = {
    info("[*] onEvent: request to update online friend cards")
    // lock use to prevent multiple calls to load list while it is already loading
    if (!lock) {
      lock = true
      getFriendsUseCase.loadOnlineFriends().map(f => runOnUiThread(view.updateCardContent(f)))
      lock = false
    }
    else info("[-] update online friends card blocked")
  }
}
