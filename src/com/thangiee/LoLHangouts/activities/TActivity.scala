package com.thangiee.LoLHangouts.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.{MenuItem, Window}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.api.utils.MemCache
import com.thangiee.LoLHangouts.services.LoLHangoutsService
import com.thangiee.LoLHangouts.utils.Events._
import com.thangiee.LoLHangouts.utils.{TContext, TLogger}
import de.greenrobot.event.EventBus
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_about    ⇒ startActivity[AboutActivity]
      case R.id.menu_changelog ⇒ showChangeLog()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  protected def cleanUpAndDisconnect() {
    info("[*]cleaning up and disconnecting...")
    EventBus.getDefault.unregister(this)
    MemCache.cleanUp()
    stopService[LoLHangoutsService]
    appCtx.resetState()
    Future (LoLChat.disconnect())
  }

  private def showChangeLog(): Unit = {
    val changeList = getLayoutInflater.inflate(R.layout.change_log_view, null)

    val dialog = new AlertDialogBuilder()
      .setView(changeList)
      .setPositiveButton(android.R.string.ok, (dialog: DialogInterface) ⇒ dialog.dismiss())
      .create()

    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.show()
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(R.color.my_dark_blue.r2Color)
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(R.color.my_orange.r2Color)
  }

  def onEvent(event: CroutonMsg): Unit = {
    event.msg.makeCrouton(event.style, event.duration)
  }
}
