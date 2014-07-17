package com.thangiee.LoLWithFriends.api

import org.jivesoftware.smack._
import android.content.Context
import scala.collection.JavaConversions._
import org.jivesoftware.smack.packet.{Message, Presence}
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.packet.Message.Type

class LoLChat(context: Context, server: Servers) {
  // set up configuration to connect
  private val config = new ConnectionConfiguration(server.host, 5223, "pvp.net")
  config.setSocketFactory(new DummySSLSocketFactory())
  Riot.init(context, config)

  def connect(): Boolean = XMPPExceptionHandler(Riot.conn.connect())

  def login(user: String, pass: String): Boolean = login(user, pass, replaceLeague = false)

  def login(user: String, pass: String, replaceLeague: Boolean): Boolean = {
    if (replaceLeague)
      XMPPExceptionHandler(Riot.conn.login(user, "AIR_"+pass, "xiff"))
    else
      XMPPExceptionHandler(Riot.conn.login(user, "AIR_"+pass))
  }

  def disconnect() = Riot.conn.disconnect()

  def friends: List[Summoner] = for (entry <- Riot.conn.getRoster.getEntries.toList) yield new Summoner(entry)

  def friendByName(name: String): Option[Summoner] = friends.find((f) => f.name == name)

  def friendById(id: String): Option[Summoner] = friends.find((f) => f.id == id)

  def onlineFriends: List[Summoner] = friends.filter((friend) => friend.isOnline)

  def offlineFriends: List[Summoner] = friends.filter((friend) => !friend.isOnline)

  def isConnected: Boolean = Riot.conn.isConnected

  def isLogin: Boolean = Riot.conn.isAuthenticated

  def appearOnline() = updateStatus(Presence.Type.available)

  def appearOffline() = updateStatus(Presence.Type.unavailable)

  def appearAway() = updateStatus(Presence.Type.available, Presence.Mode.away)

  def sendMessage(summoner: Summoner, msg: String):Boolean = {
    val message = new Message(summoner.id, Type.chat)
    message.setBody(msg)
    XMPPExceptionHandler(Riot.conn.sendPacket(message))
  }

  def initListener(listener: MessageListener) {
    Riot.conn.getChatManager.addChatListener(new ChatManagerListener {
      override def chatCreated(chat: Chat, createdLocally: Boolean): Unit = {
        if (!createdLocally)
          chat.addMessageListener(listener)
      }
    })
  }

  private[api] def updateStatus(`type`: Presence.Type, mode: Presence.Mode = Mode.chat) {
    val p = new Presence(`type`, "This is a Test", 1, mode) // todo: change message
    try {
      Riot.conn.sendPacket(p)
    } catch {
      case e: IllegalStateException => e.printStackTrace()
    }
  }

  private[api] def XMPPExceptionHandler(function: => Unit): Boolean = {
    try {
      function; true
    } catch {
      case e: XMPPException => false
      case e: Exception => false
    }
  }
}
