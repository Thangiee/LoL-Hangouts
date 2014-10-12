package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import com.thangiee.LoLHangouts.utils.Events._
import com.thangiee.LoLHangouts.utils.{TContext, TLogger}

trait TActivity extends org.scaloid.common.SActivity with TContext with TLogger {

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    if (b != null) {
      appCtx.currentUser = b.getString("user")
      appCtx.activeFriendChat = b.getString("friend-chat")
    }
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    super.onSaveInstanceState(outState)
    outState.putString("user", appCtx.currentUser)
    outState.putString("friend-chat", appCtx.activeFriendChat)
  }

  override def onResume(): Unit = {
    croutonEventBus.registerSticky(this)
    super.onResume()
  }

  override def onPause(): Unit = {
    croutonEventBus.unregister(this)
    super.onPause()
  }

  override def onStop(): Unit = {
    System.gc()
    super.onStop()
  }

  def onEvent(event: CroutonMsg): Unit = {
    event.msg.makeCrouton(event.style, event.duration)
  }
}
