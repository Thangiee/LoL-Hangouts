package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.MessageEntity
import com.thangiee.LoLHangouts.domain.entities.Message

case class MessageMapper() {

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
