package com.thangiee.LoLHangouts.utils

import com.ruenzuo.messageslistview.models.Message
import com.thangiee.LoLHangouts.api.Summoner

object Events {
  case class RefreshFriendList()
  case class ReceivedMessage(summoner: Summoner, msg: Message)
  case class SummonerCardClicked(summoner: Summoner)
  case class RefreshSummonerCard(summoner: Summoner)
  case class ClearChatNotification()
  case class ClearLoginNotification()
  case class ClearDisConnectNotification()
  case class FinishMainActivity()
}
