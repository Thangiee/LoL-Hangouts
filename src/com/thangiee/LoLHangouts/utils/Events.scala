package com.thangiee.LoLHangouts.utils

import com.ruenzuo.messageslistview.models.Message
import com.thangiee.LoLHangouts.api.core.Friend

object Events {
  case class RefreshFriendList()
  case class ReceivedMessage(friend: Friend, msg: Message)
  case class FriendCardClicked(friend: Friend)
  case class RefreshFriendCard(friend: Friend)
  case class ClearChatNotification()
  case class ClearLoginNotification()
  case class ClearDisConnectNotification()
  case class FinishMainActivity()
  case class ShowNiftyNotification(msg: Message)
}
