package com.thangiee.LoLHangouts.activities

import android.content.{Context, Intent}
import android.os.Bundle
import android.widget.LinearLayout
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.interactor.GetFriendsUseCaseImpl
import com.thangiee.LoLHangouts.ui.friendchat.ChatView
import com.thangiee.LoLHangouts.utils.Events.ClearChatNotification
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class QuickChatActivity extends TActivity with UpButton {
  override val layoutId              = R.layout.act_with_container_shadowed

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val friendName = getIntent.getStringExtra("name-key")

    for {
      friend ← GetFriendsUseCaseImpl().loadFriendByName(friendName)
    } yield runOnUiThread {
      val chatView = new ChatView()
      find[LinearLayout](R.id.content_container).addView(chatView)
      chatView.setFriend(friend)
    }

    EventBus.getDefault.post(ClearChatNotification())
  }
}

object QuickChatActivity extends TIntent {
  def apply(name: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[QuickChatActivity]).args("name-key" -> name)
  }
}
