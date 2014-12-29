package com.thangiee.LoLHangouts.domain.entities

import java.sql.Date

case class Message(
  sender: String,
  receiver: String,
  text: String,
  isSentByUser: Boolean,
  isRead: Boolean,
  date: Date
  ) // todo: change date lib
