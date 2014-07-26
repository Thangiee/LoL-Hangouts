package com.thangiee.LoLWithFriends.utils

import java.util

import com.activeandroid.query.Select
import com.ruenzuo.messageslistview.models.Message
import scala.collection.JavaConversions._

object DataBaseHandler {

  def getMessageLog(userName:String, otherName: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName)
      .execute[Message]()
  }

  def getLastMessage(userName:String, otherName: String): Message = {
    getMessageLog(userName, otherName).reduceRight((m1, m2) => if (m1.getDate.after(m2.getDate)) m1 else m2)
  }
}
