package com.thangiee.LoLWithFriends.utils

import java.util

import com.activeandroid.query.Select
import com.ruenzuo.messageslistview.models.Message

object DataBaseHandler {

  def getMessageLog(name: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("name = ?", name).execute[Message]()
  }
}
