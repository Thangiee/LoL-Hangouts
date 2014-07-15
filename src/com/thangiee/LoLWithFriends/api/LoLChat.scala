package com.thangiee.LoLWithFriends.api

import org.jivesoftware.smack.{XMPPException, XMPPConnection, ConnectionConfiguration, SmackAndroid}
import android.content.Context
import scala.collection.JavaConversions._
import scala.Option
import org.jivesoftware.smack.packet.Presence.Type

class LoLChat(context: Context, server: Servers) {
  SmackAndroid.init(context) // must call before using asmack

  // set up configuration to connect
  private val config = new ConnectionConfiguration(server.host, 5223, "pvp.net")
  config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled)
  config.setSocketFactory(new DummySSLSocketFactory())
  config.setCompressionEnabled(true)
  private val connection = new XMPPConnection(config)

  def connect(): Boolean = {
    XMPPExceptionHandler(connection.connect())
  }

  def login(user: String, pass: String): Boolean = {
    XMPPExceptionHandler(connection.login(user, "AIR_"+pass))
  }

  def disconnect() {
    connection.disconnect()
  }

  def getFriends: List[Friend] = {
    for (entry <- connection.getRoster.getEntries.toList) yield new Friend(entry)
  }

  def getFriendByName(name: String): Option[Friend] = {
    getFriends.find((f) => f.name == name)
  }

  private def XMPPExceptionHandler(function: => Unit): Boolean = {
    try {
      function; true
    } catch {
      case e: XMPPException => false
    }
  }
}
