package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.MessageEntity
import com.thangiee.lolhangouts.domain.entities.Message

object MessageMapper {

  def transform(messageEntity: MessageEntity): Message = {
    Message(
      messageEntity.friendName,
      messageEntity.text,
      messageEntity.isSentByUser,
      messageEntity.isRead,
      messageEntity.date
    )
  }
}
