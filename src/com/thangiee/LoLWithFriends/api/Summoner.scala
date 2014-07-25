package com.thangiee.LoLWithFriends.api

import org.jivesoftware.smack.RosterEntry
import org.jivesoftware.smack.packet.Presence.Type
import org.jivesoftware.smack.packet.Presence

class Summoner(entry: RosterEntry) {

  def name: String = entry.getName

  def id: String = entry.getUser

  def chatMode: Presence.Mode = presence.getMode

  def chatType: Presence.Type = presence.getType

  def isOnline: Boolean = presence.getType == Type.available

  def status: String = if (isOnline) presence.getStatus else ""

  private def presence: Presence = LoLChat.connection.getRoster.getPresence(id)
}
