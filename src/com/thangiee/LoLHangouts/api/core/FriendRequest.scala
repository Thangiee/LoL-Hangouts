package com.thangiee.LoLHangouts.api.core

import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.packet.{Packet, Presence}

object FriendRequest {

  /**
   * process the incoming friend requests
   */
  case class Listener() extends PacketListener {
    override def processPacket(p: Packet): Unit = {
      LoLChat.friendListListener.onFriendRequest(p.getFrom, "[0-9]+".r.findFirstIn(p.getFrom).getOrElse("0"))
    }
  }

  /**
   * Filter for only friend requests i.e. only subscribe packets
   */
  case class Filter() extends PacketFilter {
    override def accept(p: Packet): Boolean = {
      p match {
        case presence: Presence => if (presence.getType.equals(Presence.Type.subscribe)) true else false
        case _ => false
      }
    }
  }
}
