package com.thangiee.LoLHangouts.data.repository.datasources.net.core

import com.thangiee.LoLHangouts.data.entities.FriendEntity
import com.thangiee.LoLHangouts.domain.entities.{NA, Region}
import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message.Type
import org.jivesoftware.smack.packet.Presence.Mode
import org.jivesoftware.smack.packet.Presence.Type.{available, unavailable}
import org.jivesoftware.smack.packet.{Message, Presence}
import org.jivesoftware.smack.tcp.XMPPTCPConnection

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

object LoLChat {
  Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual)
  private var _connection        : Option[XMPPConnection]     = None
  private var _friendListListener: Option[FriendListListener] = None
  private var _connectionListListener: Option[ConnectionListener] = None
  private var _statusMsg    = "Using LoL Hangouts App"
  private var _presenceMode = Mode.away
  private var _presenceType = unavailable
  private var _loginName    = ""
  private var _region: Region = NA
  private var rosterListener: Option[RosterListener] = None

  /**
   * @return the connected XMPPConnection 
   * @throws IllegalStateException if connection is not connected 
   */
  def connection: XMPPConnection = _connection.getOrElse(throw new IllegalStateException(
    "Connection is not setup! Make sure you call LoLChat.connect(...) first."))

  /**
   * @return the listener  
   * @throws IllegalStateException if LoLChat.initFriendListListener() was not called before 
   */
  def friendListListener: FriendListListener = _friendListListener.getOrElse(throw new IllegalStateException(
    "Listener is not setup! Make sure you call LoLChat.initFriendListListener(...)"))

  /**
   * Connect to Riot PVP chat server
   *
   * @param region the region to connect
   * @return true if connected to server, otherwise false
   */
  def connect(region: Region): Boolean = {
    // set up configuration to connect
    val config = new ConnectionConfiguration(region.url, 5223, "pvp.net") // url ex. chat.na1.lol.riotgames.com
    config.setSocketFactory(new DummySSLSocketFactory())
    config.setRosterLoadedAtLogin(true)
    config.setSendPresence(true)
    config.setReconnectionAllowed(true)
    _connection = Some(new XMPPTCPConnection(config))
    connection.addPacketListener(FriendRequest.Listener(), FriendRequest.Filter())
    _region = region
    XMPPExceptionHandler(connection.connect())
  }

  /**
   * Log the user into Riot PVP chat server
   *
   * @param user account username
   * @param pass account password
   * @return true if logged into to server, otherwise false
   */
  def login(user: String, pass: String): Boolean = login(user, pass, replaceLeague = false)

  def login(user: String, pass: String, replaceLeague: Boolean): Boolean = {
    _loginName = user
    val isLogin = if (replaceLeague)
      XMPPExceptionHandler(connection.login(user, "AIR_" + pass, "xiff"))
    else
      XMPPExceptionHandler(connection.login(user, "AIR_" + pass))

    if (isLogin) {
      connection.getRoster //todo: weird bug; need to call this or else all friends appear offline after login... :\
      true
    } else {
      false
    }
  }

  /**
    * @return summoner ID
    */
  def summonerId(): Option[String] = "[0-9]+".r.findFirstIn(connection.getUser)

  /**
   * Disconnect from the chat server
   */
  def disconnect() = {
    _connectionListListener.map(connection.removeConnectionListener)
    rosterListener.map(connection.getRoster.removeRosterListener)
    _connectionListListener = None
    rosterListener = None
    connection.disconnect()
    _statusMsg = "Using LoL Hangouts App"
  }

  /**
   * @return list of friends from the user's friend List
   */
  def friends: List[FriendEntity] = connection.getRoster.getEntries.toList.map(entry ⇒ new FriendEntity(entry))

  /**
   * Search the friend list by name
   *
   * @param name the name to search for
   * @return the friend if found
   */
  def getFriendByName(name: String): Option[FriendEntity] = friends.find((f) => f.name == name)

  /**
   * Search the friend list by id 
   *
   * @param id the id to search for
   * @return the friend if found
   */
  def getFriendById(id: String): Option[FriendEntity] = friends.find((f) => f.addr.contains(id))

  /**
   * @return all online friends in the user's friend list
   */
  def onlineFriends: List[FriendEntity] = friends.filter((friend) => friend.isOnline)

  /**
   * @return all offline friends in the user's friend list
   */
  def offlineFriends: List[FriendEntity] = friends.filter((friend) => !friend.isOnline)

  /**
   * @return true if the connection is connected 
   */
  def isConnected: Boolean = Try(connection) match {
    case Success(c) ⇒ c.isConnected
    case Failure(e) ⇒ false
  }

  /**
   * @return true if the user is logged into the server
   */
  def isLogin: Boolean = Try(connection) match {
    case Success(c) ⇒ c.isAuthenticated
    case Failure(e) ⇒ false
  }

  /**
   * appear online to all friends (green circle)
   */
  def appearOnline() = updateStatus(available)

  /**
   * appear offline to all friends (user is still connected to server)
   */
  def appearOffline() = updateStatus(unavailable)

  /**
   * appear away to all friends (red circle)
   */
  def appearAway() = updateStatus(Presence.Type.available, Presence.Mode.away)

  /**
   * The two important type:
   * available - logged in 
   * unavailable - logged out
   *
   * @return presence type
   */
  def presenceType() = _presenceType

  /**
   * Send a text message to a friend
   *
   * @param friend recipient of the message
   * @param msg a text message
   * @return true if the message was delivered to the friend
   */
  def sendMessage(friend: FriendEntity, msg: String): Boolean = {
    val message = new Message(friend.addr, Type.chat)
    message.setBody(msg)
    XMPPExceptionHandler(connection.sendPacket(message))
  }

  /**
   * @return your status message that all your friends see in their friend list
   */
  def statusMsg(): String = _statusMsg

  /**
   * change your status message
   *
   * @param msg the message that all your friends see in their friend list
   */
  def changeStatusMsg(msg: String) { _statusMsg = msg; updateStatus(available, _presenceMode) }

  /**
   * Setup the listener for all incoming and outgoing messages
   *
   * @param listener the message listener
   */
  def initChatListener(listener: MessageListener) {
    ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListener {
      override def chatCreated(chat: Chat, createdLocally: Boolean): Unit = {
        if (!createdLocally)
          chat.addMessageListener(listener)
      }
    })
  }

  def loginName(): String = _loginName

  def region(): Region = _region

  /**
   * Setup the listener that notify when a friend status change i.e. login, logout, enter queue, etc...
   *
   * @param listener the message listener
   */
  def initFriendListListener(listener: FriendListListener) {
    _friendListListener = Some(listener)
    rosterListener = Some(new FriendRosterListener())
    connection.getRoster.addRosterListener(rosterListener.get)
  }

  def initConnectionListener(listener: ConnectionListener): Unit = {
    _connectionListListener = Some(listener)
    connection.addConnectionListener(listener)
  }

  private[net] def updateStatus(`type`: Presence.Type, mode: Mode = Mode.chat) {
    _presenceType = `type`
    _presenceMode = mode
    // default status message
    val status = "<body>" +
      "<profileIcon>" + 1 + "</profileIcon>" +
      "<level>" + 30 + "</level>" +
      "<wins>" + 1337 + "</wins>" +
      "<tier>CHALLENGER</tier>" +
      "<rankedLeagueDivision>" + "I" + "</rankedLeagueDivision>" +
      "<rankedLeagueTier>" + "CHALLENGER" + "</rankedLeagueTier>" +
      "<rankedLeagueQueue>" + "RANKED_SOLO_5x5" + "</rankedLeagueQueue>" +
      "<rankedWins>" + 1337 + "</rankedWins>" +
      "<statusMsg>" + _statusMsg + "</statusMsg>" +
      "<gameStatus>outOfGame</gameStatus>" +
      "</body>"

    val p = new Presence(`type`, status, 1, mode)
    try {
      connection.sendPacket(p)
    } catch {
      case e: IllegalStateException => e.printStackTrace()
    }
  }

  private[net] def XMPPExceptionHandler(function: => Unit): Boolean = {
    try {
      function; true
    } catch {
      case e: XMPPException => e.printStackTrace(); false
      case e: Exception => e.printStackTrace(); false
    }
  }
}
