package com.thangiee.LoLHangouts

import com.thangiee.LoLHangouts.api.Server

object MyApp {
  var selectedServer: Server = _
  var currentUser = ""
  var isFriendListOpen = false
  var isChatOpen = false
  var activeFriendChat = ""

  def reset() {
    currentUser = ""
    isFriendListOpen = false
    isChatOpen = false
    activeFriendChat = ""
  }
}
