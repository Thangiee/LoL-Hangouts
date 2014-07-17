package com.thangiee.LoLWithFriends.api

import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message.Type
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.packet.{Message, Presence}

import scala.collection.JavaConversions._

object LoLChat {
  private var _connection: Option[XMPPConnection] = None

  def connection: XMPPConnection = {
    _connection match {
      case Some(conn) => conn
      case None       => throw new IllegalStateException("Connection is not setup! Make sure you call connect() first.")
    }
  }

  def connect(server: Servers): Boolean = {
    // set up configuration to connect
    val config = new ConnectionConfiguration(server.host, 5223, "pvp.net")
    config.setSocketFactory(new DummySSLSocketFactory())
    _connection = Some(new XMPPConnection(config))
    XMPPExceptionHandler(connection.connect())
  }

  def login(user: String, pass: String): Boolean = login(user, pass, replaceLeague = false)

  def login(user: String, pass: String, replaceLeague: Boolean): Boolean = {
    if (replaceLeague)
      XMPPExceptionHandler(connection.login(user, "AIR_"+pass))
    else
      XMPPExceptionHandler(connection.login(user, "AIR_"+pass, "xiff"))
  }

  def disconnect() = connection.disconnect()

  def friends: List[Summoner] = for (entry <- connection.getRoster.getEntries.toList) yield new Summoner(entry)

  def friendByName(name: String): Option[Summoner] = friends.find((f) => f.name == name)

  def friendById(id: String): Option[Summoner] = friends.find((f) => f.id == id)

  def onlineFriends: List[Summoner] = friends.filter((friend) => friend.isOnline)

  def offlineFriends: List[Summoner] = friends.filter((friend) => !friend.isOnline)

  def isConnected: Boolean = connection.isConnected

  def isLogin: Boolean = connection.isAuthenticated

  def appearOnline() = updateStatus(Presence.Type.available)

  def appearOffline() = updateStatus(Presence.Type.unavailable)

  def appearAway() = updateStatus(Presence.Type.available, Presence.Mode.away)

  def sendMessage(summoner: Summoner, msg: String):Boolean = {
    val message = new Message(summoner.id, Type.chat)
    message.setBody(msg)
    XMPPExceptionHandler(connection.sendPacket(message))
  }

  def initListener(listener: MessageListener) {
    connection.getChatManager.addChatListener(new ChatManagerListener {
      override def chatCreated(chat: Chat, createdLocally: Boolean): Unit = {
        if (!createdLocally)
          chat.addMessageListener(listener)
      }
    })
  }

  private[api] def updateStatus(`type`: Presence.Type, mode: Presence.Mode = Mode.chat) {
    val p = new Presence(`type`, "This is a Test", 1, mode) // todo: change message
    try {
      connection.sendPacket(p)
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
