package com.thangiee.lolhangouts.ui.friendchat

import com.github.nscala_time.time.Imports._
import com.thangiee.lolhangouts.data.usecases.ManageFriendUseCase._
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.usecases.{GetUserUseCase, ManageFriendUseCase, GetFriendsUseCase}
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils.Events.{CreateFriendGroup, ReloadFriendCardList, UpdateFriendCard, UpdateOnlineFriendsCard}
import com.thangiee.lolhangouts.ui.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.{Future, duration, Await}
import scala.concurrent.ExecutionContext.Implicits.global

class FriendListPresenter(view: FriendListView, getFriendsUseCase: GetFriendsUseCase,
                          manageFriendUseCase: ManageFriendUseCase, getUserUseCase: GetUserUseCase) extends Presenter {

  private var lock           = false
  private val autoUpdateTask = new Runnable {
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
    loadFriendList()
  }

  override def pause(): Unit = {
    super.pause()
    handler.removeCallbacksAndMessages(null)
  }

  override def shutdown(): Unit = {
    super.shutdown()
    EventBus.getDefault.unregister(this)
  }

  def sendFriendRequest(toName: CharSequence): Unit = {
    manageFriendUseCase.sendFriendRequest(toName.toString).onSuccess {
      case Good(_) => view.showFriendRequestSent()
      case Bad(FriendNotFound)  => view.showInvalidNameError()
      case Bad(NoConnection)  => view.showNoConnectionError()
      case Bad(InternalError) => view.showUnexpectedError()
    }
  }

  def createNewGroup(groupName: CharSequence): Unit = {
    manageFriendUseCase.createGroup(groupName.toString).onSuccess {
      case Good(_) =>
        EventBus.getDefault.post(CreateFriendGroup(groupName.toString))
        view.showNewGroupCreated(groupName.toString)
      case Bad(InternalError) => view.showUnexpectedError()
    }
  }

  def moveFriends(friendsName: Set[CharSequence], groupName: String): Unit = {
    val f = friendsName.map(name => manageFriendUseCase.moveFriendToGroup(name.toString, groupName))
    Await.ready(Future.sequence(f), duration.Duration.Inf)
    loadFriendList()
    view.showFriendsMoved(friendsName.size, groupName)
  }

  def getFriends: Seq[Friend] =
    Await.result(getFriendsUseCase.loadFriendList(), duration.Duration.Inf)

  def getGroupsName: Seq[CharSequence] =
    Await.result(getUserUseCase.loadUser(), duration.Duration.Inf).map(_.groupNames).getOrElse(Nil)

  private def loadFriendList(): Unit = {
    // lock use to prevent multiple calls to load list while it is already loading
    if (!lock) {
      lock = true
      info("[*] loading friends")
      getFriendsUseCase.loadFriendList().map { friends =>
        val filteredFriends = view.friendGroupToShow.toLowerCase match {
          case "all"     => friends
          case "online"  => friends.filter(_.isOnline)
          case "offline" => friends.filter(!_.isOnline)
          case groupName => friends.filter(_.groupName.toLowerCase == groupName)
        }

        val (onFriends, offFriends) = filteredFriends.partition(_.isOnline)
        runOnUiThread(view.initCardList(onFriends, offFriends))
        lock = false
      }
    }
    else info("[-] repopulate friend card list blocked")
  }

  def onEvent(event: ReloadFriendCardList): Unit = {
    info("[*] onEvent: request to reload friend list")
    loadFriendList()
  }

  def onEvent(event: UpdateFriendCard): Unit = {
    info("[*] onEvent: request to update " + event.friendName + "friend card")

    // block RefreshFriendCard event when friend list is currently loading 
    if (!lock)
      getFriendsUseCase.loadFriendByName(event.friendName).onSuccess {
        case Good(f) => runOnUiThread(view.updateCardContent(f))
      }
    else
      info("[-] Refresh friend card blocked")
  }

  def onEvent(events: UpdateOnlineFriendsCard): Unit = {
    info("[*] onEvent: request to update online friend cards")
    // lock use to prevent multiple calls to load list while it is already loading
    if (!lock) {
      lock = true
      getFriendsUseCase.loadOnlineFriends().onSuccess {
        case fl => fl.foreach(f => runOnUiThread(view.updateCardContent(f)))
      }
      lock = false
    }
    else info("[-] update online friends card blocked")
  }
}
