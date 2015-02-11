package com.thangiee.lolhangouts.ui.friendchat

import android.content.{Context, Intent}
import android.os.Bundle
import android.widget.LinearLayout
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.repository._
import com.thangiee.lolhangouts.domain.interactor.GetFriendsUseCaseImpl
import com.thangiee.lolhangouts.ui.core.{TActivity, TIntent, UpButton}
import com.thangiee.lolhangouts.utils.Events.ClearChatNotification
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class QuickChatActivity extends TActivity with UpButton {
  override val layoutId              = R.layout.act_with_container_shadowed

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val friendName = getIntent.getStringExtra("name-key")
    setTitle(friendName)

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