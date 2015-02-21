package com.thangiee.lolhangouts.domain.entities

import java.util.Date


case class Message(
  friendName: String,
  text: String,
  isSentByUser: Boolean,
  isRead: Boolean,
  date: Date
  )
