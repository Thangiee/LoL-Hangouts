package com.thangiee.LoLHangouts.data.entities

import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import org.jivesoftware.smack.RosterEntry
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Type

class FriendEntity(val entry: RosterEntry) {
  val name: String = entry.getName

  /**
   * ex sum123456@pvp.net
   */
  val addr  : String = entry.getUser

  /**
   *  chat -> green circle
   *  away -> red circle
   *  dnd  -> yellow circle
   */
  def chatMode: Presence.Mode = presence.getMode

  /**
   * available    -> online
   * unavailable  -> offline
   */
  def chatType: Presence.Type = presence.getType

  /**
   * @return true if this friend is online
   */
  def isOnline: Boolean = presence.getType == Type.available

  /**
   * @return information about this friend
   * @see LoLStatus$ for the information you can get
   */
  def status: String = if (isOnline) presence.getStatus else ""

  private def presence: Presence = LoLChat.connection.getRoster.getPresence(addr)
}
