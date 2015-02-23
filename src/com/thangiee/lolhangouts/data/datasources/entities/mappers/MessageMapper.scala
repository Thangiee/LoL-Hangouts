package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.MessageEntity
import com.thangiee.lolhangouts.data.usecases.entities.Message

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
