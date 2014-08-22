package com.thangiee.LoLHangouts.utils

import java.util

import com.activeandroid.query.{Delete, Select}
import com.ruenzuo.messageslistview.models.Message

import scala.collection.JavaConversions._

object DataBaseHandler {

  def getAllMessages: List[Message] = new Select().from(classOf[Message]).execute[Message]().toList

  def getMessageLog(userName:String, otherName: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName)
      .execute[Message]()
  }

  def deleteMessageLog(userName:String, otherName: String) {
    new Delete().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName).execute()
  }

  def getLastMessage(userName:String, otherName: String): Option[Message] = {
    val msgLog = getMessageLog(userName, otherName)
    if (!msgLog.isEmpty) Some(msgLog.last) else None
  }

  def getUnReadMessages(userName:String): List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND isRead = 0", userName).execute[Message]().toList
  }
}
