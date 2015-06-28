package com.thangiee.lolhangouts.ui.friendchat

import android.content.{Context, Intent}
import android.os.Bundle
import android.widget.LinearLayout
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.GetFriendsUseCaseImpl
import com.thangiee.lolhangouts.ui.core.{TActivity, TIntent, UpButton}
import com.thangiee.lolhangouts.ui.utils.Events.ClearChatNotification
import de.greenrobot.event.EventBus
import org.scalactic.Good

import scala.concurrent.ExecutionContext.Implicits.global

class QuickChatActivity extends TActivity with UpButton {
  override val layoutId = R.layout.act_with_container_shadowed
  override val snackBarHolderId = R.id.act_with_container_shadowed

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val friendName = getIntent.getStringExtra("name-key")
    setTitle(friendName)

    GetFriendsUseCaseImpl().loadFriendByName(friendName).onSuccess {
      case Good(friend) => runOnUiThread {
        val chatView = new ChatView()
        find[LinearLayout](R.id.content_container).addView(chatView)
        chatView.setFriend(friend)
      }
    }

    EventBus.getDefault.post(ClearChatNotification())
  }
}

object QuickChatActivity extends TIntent {
  def apply(name: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[QuickChatActivity]).args("name-key" -> name)
  }
}
