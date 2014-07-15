package com.thangiee.LoLWithFriends.api

import org.jivesoftware.smack.RosterEntry

class Friend(entry: RosterEntry) {

  def name: String = {
    entry.getName
  }
}
