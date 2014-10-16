package com.thangiee.LoLHangouts.utils

import com.ruenzuo.messageslistview.models.Message
import com.thangiee.LoLHangouts.api.core.Friend
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Configuration, Style}

object Events {
  val croutonEventBus = new EventBus()
  val niftyNotificationEventBus = new EventBus()
  case class CroutonMsg(msg: String, style: Style = Style.CONFIRM, duration: Int = Configuration.DURATION_LONG)
  case class RefreshFriendList()
  case class IncomingMessage(from: Friend, msg: Message)
  case class FriendCardClicked(friend: Friend)
  case class RefreshFriendCard(friend: Friend)
  case class ClearChatNotification()
  case class ClearLoginNotification()
  case class ClearDisConnectNotification()
  case class FinishMainActivity()
  case class ShowNiftyNotification(msg: Message)
}
