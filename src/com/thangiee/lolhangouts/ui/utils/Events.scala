package com.thangiee.lolhangouts.ui.utils

import com.thangiee.lolhangouts.data.usecases.entities.{Message, Friend}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Configuration, Style}

object Events {
  val croutonEventBus = new EventBus()
  val niftyNotificationEventBus = new EventBus()
  case class CroutonMsg(msg: String, style: Style = Style.CONFIRM, duration: Int = Configuration.DURATION_LONG)
  case class IncomingMessage(from: Friend, msg: Message)
  case class FriendCardClicked(friend: Friend)
  case class ReloadFriendCardList()
  case class UpdateFriendCard(friendName: String)
  case class UpdateOnlineFriendsCard()
  case class ClearChatNotification()
  case class ClearLoginNotification()
  case class ClearDisConnectNotification()
  case class FinishActivity()
  case class Logout()
  case class ShowNiftyNotification(msg: Message)
  case class SwitchScreen(drawerTitle: String)
  case class ShowDisconnection()
}
