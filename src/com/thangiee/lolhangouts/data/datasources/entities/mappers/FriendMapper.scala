package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.cache.{CacheKey, PrefsCache}
import com.thangiee.lolhangouts.data.datasources.entities.FriendEntity
import com.thangiee.lolhangouts.data.datasources.net.core.{LoLChat, LoLStatus}
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.data.usecases.entities.ChatMode.ChatMode
import com.thangiee.lolhangouts.data.usecases.entities.ChatType.ChatType
import com.thangiee.lolhangouts.data.usecases.entities.{ChatMode, ChatType, Friend}
import org.jivesoftware.smack.packet.Presence

object FriendMapper {

  def transform(friendEntity: FriendEntity): Friend = {
    Friend(
      friendEntity.name,
      "[0-9]+".r.findFirstIn(friendEntity.addr).getOrElse("0"),
      PrefsCache.getString(CacheKey.LoginRegionId).getOrElse(""),
      DB.getLatestMessage(LoLChat.loginName, friendEntity.name).map(m => Some(MessageMapper.transform(m))).getOrElse(None),
      transformChatMode(friendEntity.chatMode),
      transformChatType(friendEntity.chatType),
      friendEntity.isOnline,
      getTimeInGame(friendEntity),
      LoLStatus.parse(friendEntity, LoLStatus.SkinName),
      LoLStatus.parse(friendEntity, LoLStatus.GameStatus).getOrElse(""),
      LoLStatus.parse(friendEntity, LoLStatus.Level).getOrElse("0"),
      LoLStatus.parse(friendEntity, LoLStatus.StatusMsg).getOrElse("No Status Message"),
      LoLStatus.parse(friendEntity, LoLStatus.RankedLeagueTier).getOrElse("UNRANKED"),
      LoLStatus.parse(friendEntity, LoLStatus.RankedLeagueDivision).getOrElse(""),
      LoLStatus.parse(friendEntity, LoLStatus.RankedLeagueName).getOrElse("NO LEAGUE"),
      LoLStatus.parse(friendEntity, LoLStatus.Wins).getOrElse("0")
    )
  }

  private def transformChatMode(mode: Presence.Mode): ChatMode = {
    mode match {
      case Presence.Mode.available => ChatMode.Chat
      case Presence.Mode.chat      => ChatMode.Chat
      case Presence.Mode.away      => ChatMode.Away
      case Presence.Mode.xa        => ChatMode.Away
      case Presence.Mode.dnd       => ChatMode.Dnd
      case _                       => ChatMode.Away
    }
  }

  private def transformChatType(mode: Presence.Type): ChatType = {
    mode match {
      case Presence.Type.available   => ChatType.Available
      case Presence.Type.unavailable => ChatType.Unavailable
      case _                         => ChatType.Unavailable
    }
  }

  private def getTimeInGame(friendEntity: FriendEntity): Long = {
    val currentTime = System.currentTimeMillis()
    val gameStartTime = LoLStatus.parse(friendEntity, LoLStatus.TimeStamp).getOrElse(currentTime.toString).toLong
    currentTime - gameStartTime
  }
}
