package com.thangiee.LoLWithFriends.api

import jriot.main.JRiot
import org.jivesoftware.smack.{SmackAndroid, XMPPConnection, ConnectionConfiguration}
import android.content.Context

object Riot {
  println(">>>>>>> START")
  var smackAndroid:SmackAndroid = _
  var conn: XMPPConnection = _
  val api = new JRiot()

  
  def init(context: Context, config: ConnectionConfiguration) = {
    smackAndroid = SmackAndroid.init(context)
    smackAndroid.onDestroy()
    api.setApiKey("api-key")
    api.setRegion("na")
    conn = new XMPPConnection(config)
  }
}
