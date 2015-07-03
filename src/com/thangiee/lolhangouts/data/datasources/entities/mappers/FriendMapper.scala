package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolchat.chatMode._
import com.thangiee.lolchat.{FriendEntity, chatMode}
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data.usecases.entities.ChatMode.ChatMode
import com.thangiee.lolhangouts.data.usecases.entities.{ChatMode, Friend}

object FriendMapper {

  def transform(friendEntity: FriendEntity): Friend = {
    Friend(
      friendEntity.name,
      friendEntity.id,
      Cached.loginRegionId.getOrElse("na"),
      DB.getLatestMessage(Cached.loginUsername, friendEntity.name)
        .map(m => Some(MessageMapper.transform(m))).getOrElse(None),
      transformChatMode(friendEntity.chatMode),
      friendEntity.isOnline,
      getTimeInGame(friendEntity),
      friendEntity.selectedChamp.toOption,
      friendEntity.gameStatus.getOrElse(""),
      friendEntity.level.toString,
      friendEntity.statusMsg,
      friendEntity.rankedTier.getOrElse("UNRANKED"),
      friendEntity.rankedDivision.getOrElse(""),
      friendEntity.leagueName.getOrElse("NO LEAGUE"),
      friendEntity.wins.toString,
      friendEntity.groupNames.headOption.getOrElse("")
    )
  }

  private def transformChatMode(mode: chatMode.ChatMode): ChatMode = {
    mode match {
      case Chat => ChatMode.Chat
      case Away => ChatMode.Away
      case Busy => ChatMode.Dnd
    }
  }

  private def getTimeInGame(friendEntity: FriendEntity): Long = {
    val currentTime = System.currentTimeMillis()
    currentTime - friendEntity.gameStartTime.getOrElse(currentTime)
  }
}
