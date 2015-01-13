package com.thangiee.LoLHangouts.data.repository.datasources.sqlite

import com.activeandroid.query.{Delete, Select}
import com.thangiee.LoLHangouts.data.entities.MessageEntity

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

/**
 * SQLite Database query handler
 */
object DB {

  /**
   * get all messages with oldest date first
   *
   * @return
   */
  def getAllMessages: List[MessageEntity] = new Select().from(classOf[MessageEntity]).execute[MessageEntity]().toList

  /**
   * get N most recent unread messages between this user and the friend ordered by date (older message first)
   *
   * @param username    name of this user
   * @param friendName  name of the other user
   * @param n           max number of messages to get
   * @return
   */
  def getMessages(username:String, friendName: String, n: Int = Int.MaxValue): List[MessageEntity] = {
    new Select().from(classOf[MessageEntity]).where("username = ? AND friendName = ?", username, friendName)
      .orderBy("date DESC").limit(n).execute[MessageEntity]().reverse.toList
  }

  /**
   * delete all messages between this user and the friend
   *
   * @param username     name of this user
   * @param friendName   name of the other user
   */
  def deleteMessages(username:String, friendName: String) {
    new Delete().from(classOf[MessageEntity]).where("username = ? AND friendName = ?", username, friendName).execute()
  }

  /**
   * get the most recent messages between this user and the friend 
   *
   * @param username    name of this user
   * @param friendName  name of the other user
   * @return
   */
  def getLatestMessage(username:String, friendName: String): Option[MessageEntity] = {
    Try {
      new Select().from(classOf[MessageEntity]).where("username = ? AND friendName = ?", username, friendName)
        .orderBy("date DESC").limit(1).executeSingle[MessageEntity]()
    } match {
      case Success(msg) => if (msg != null) Some(msg) else None
      case Failure(e)   => None
    }
  }

  /**
   * get N most recent unread messages sent to the user from any friends ordered by date (older message first)
   *
   * @param username  who the message is sent to
   * @param n         max number of messages to get
   * @return
   */
  def getUnreadMessages(username:String, n: Int = Int.MaxValue): List[MessageEntity] = {
    new Select().from(classOf[MessageEntity]).where("username = ? AND isRead = 0", username)
      .orderBy("date DESC").limit(n).execute[MessageEntity]().reverse.toList
  }

  /**
   * get all unread messages sent to the user from a friend ordered by date (older message first)
   *
   * @param username    who the message is sent to
   * @param friendName  who the message is sent by
   * @return
   */
  def getUnreadMessages(username: String, friendName: String): List[MessageEntity] = {
    new Select().from(classOf[MessageEntity]).where("username = ? AND friendName = ? AND isRead = 0", username, friendName)
      .orderBy("date DESC").execute[MessageEntity]().reverse.toList
  }
}
