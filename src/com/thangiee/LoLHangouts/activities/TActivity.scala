package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import com.thangiee.LoLHangouts.api.Region
import com.thangiee.LoLHangouts.utils.{TContext, TLogger}

trait TActivity extends org.scaloid.common.SActivity with TContext with TLogger {

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    if (b != null) {
      appCtx.selectedRegion = Region.getFromString(b.getString("region"))
      appCtx.currentUser = b.getString("user")
      appCtx.activeFriendChat = b.getString("friend-chat")
    }
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    outState.putString("region", appCtx.selectedRegion.name)
    outState.putString("user", appCtx.currentUser)
    outState.putString("friend-chat", appCtx.activeFriendChat)
  }
}
