package com.thangiee.LoLWithFriends.utils

import com.ruenzuo.messageslistview.models.Message
import com.thangiee.LoLWithFriends.api.Summoner

object Events {
  case class RefreshFriendList()
  case class ReceivedMessage(summoner: Summoner, msg: Message)
  case class SummonerCardClicked(summoner: Summoner)
}
