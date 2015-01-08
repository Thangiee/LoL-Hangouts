package com.thangiee.LoLHangouts.domain.entities

import java.util.Date


case class Message(
  friendName: String,
  text: String,
  isSentByUser: Boolean,
  isRead: Boolean,
  date: Date
  ) // todo: use better date lib
