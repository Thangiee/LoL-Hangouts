package com.thangiee.LoLHangouts.utils

import java.util

import com.activeandroid.query.{Delete, Select}
import com.ruenzuo.messageslistview.models.Message

import scala.collection.JavaConversions._

object DataBaseHandler {

  def getAllMessages: List[Message] = new Select().from(classOf[Message]).execute[Message]().toList

  def getMessages(userName:String, otherName: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName)
      .execute[Message]()
  }

  def deleteMessages(userName:String, otherName: String) {
    new Delete().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName).execute()
  }

  def getLastMessage(userName:String, otherName: String): Option[Message] = {
    val msgLog = getMessages(userName, otherName)
    if (!msgLog.isEmpty) Some(msgLog.last) else None
  }

  def getAllUnReadMessages(userName:String): List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND isRead = 0", userName).execute[Message]().toList
  }

  def getUnReadMessages(userName:String, otherName: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ? AND isRead = 0", userName, otherName)
      .execute[Message]()
  }
}
