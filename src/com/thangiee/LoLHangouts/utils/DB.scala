package com.thangiee.LoLHangouts.utils

import java.util

import com.activeandroid.query.{Delete, Select}
import com.ruenzuo.messageslistview.models.Message

import scala.collection.JavaConversions._

/**
 * SQLite Database query handler
 */
object DB {

  /**
   * get all messages with oldest date first
   *
   * @return
   */
  def getAllMessages: List[Message] = new Select().from(classOf[Message]).execute[Message]().toList

  /**
   * get N most recent unread messages between this user and the other user ordered by date (older message first)
   *
   * @param username    name of this user
   * @param otherName   name of the other user
   * @param n           max number of messages to get
   * @return
   */
  def getMessages(username:String, otherName: String, n: Int = Int.MaxValue): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", username, otherName)
      .orderBy("date DESC").limit(n).execute[Message]().reverse
  }

  /**
   * delete all messages between this user and the other user
   *
   * @param username    name of this user
   * @param otherName   name of the other user 
   */
  def deleteMessages(username:String, otherName: String) {
    new Delete().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", username, otherName).execute()
  }

  /**
   * get the most recent messages between this user and the other user
   *
   * @param username    name of this user
   * @param otherName   name of the other user 
   * @return
   */
  def getLastMessage(username:String, otherName: String): Option[Message] = {
    val lastMsg = new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ?", username, otherName)
                      .orderBy("date DESC").limit(1).executeSingle[Message]()
    if (lastMsg != null) Some(lastMsg) else None
  }

  /**
   * get N most recent unread messages sent to the recipient from anyone ordered by date (older message first)
   *  
   * @param recipient who the message is sent to
   * @param n         max number of messages to get
   * @return
   */
  def getUnreadMessages(recipient:String, n: Int = Int.MaxValue): List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND isRead = 0", recipient)
      .orderBy("date DESC").limit(n).execute[Message]().reverse.toList
  }

  /**
   * get all most recent unread messages from the sender to the recipient ordered by date (older message first)
   *
   * @param recipient who the message is sent to
   * @param sender    who the message is sent by
   * @return
   */
  def getUnreadMessages(recipient: String, sender: String): util.List[Message] = {
    new Select().from(classOf[Message]).where("thisPerson = ? AND otherPerson = ? AND isRead = 0", recipient, sender)
      .orderBy("date DESC").execute[Message]().reverse
  }
}