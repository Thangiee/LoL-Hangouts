package com.thangiee.LoLWithFriends.utils

import java.util

import com.activeandroid.query.{Delete, Select}
import com.ruenzuo.messageslistview.models.Message
import com.thangiee.LoLWithFriends.MyApp

import scala.collection.JavaConversions._

object DataBaseHandler {

  def getMessageLog(userName:String, otherName: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName)
      .execute[Message]()
  }

  def getMessageLog: util.List[Message] = getMessageLog(MyApp.currentUser, MyApp.activeFriendChat)

  def deleteMessageLog(userName:String, otherName: String) {
    new Delete().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", userName, otherName).execute()
  }

  def deleteMessageLog(): Unit = deleteMessageLog(MyApp.currentUser, MyApp.activeFriendChat)

  def getLastMessage(userName:String, otherName: String): Option[Message] = {
    val msgLog = getMessageLog(userName, otherName)
    if (!msgLog.isEmpty) Some(msgLog.last) else None
  }

  def getLastMessage: Option[Message] = getLastMessage(MyApp.currentUser, MyApp.activeFriendChat)

  def getUnReadMessages: List[Message] = {
    new Select().from(classOf[Message]).where("isRead = 0").execute[Message]().toList
  }
}
