package com.thangiee.lolhangouts.ui.friendchat

import android.content.Context
import android.graphics.Typeface
import android.view.{MotionEvent, View, ViewGroup}
import android.widget.TextView
import at.markushi.ui.RevealColorView
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.domain.entities.Friend
import com.thangiee.lolhangouts.utils.{Events, _}
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.view.CardViewNative
import org.scaloid.common.{TagUtil, TraitView}

import scala.util.Try

abstract class FriendBaseCard(private var friend: Friend, layoutId: Int)(implicit ctx: Context) extends Card(ctx, layoutId)
with TraitView[CardViewNative] with TagUtil {

  override def basis: CardViewNative = getCardView.asInstanceOf[CardViewNative]

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    super.setupInnerViewElements(parent, view)
    view.find[TextView](R.id.tv_friend_name).setText(friend.name)

    // ripple effects
    val revealView = view.find[RevealColorView](R.id.reveal)
    val gestureDetector = GestureDetectorBuilder()
      .onLongPress((e) => revealView.ripple(e.getX, e.getY, duration = 1000))
      .build

    view.onTouch((v, event) => {
      if (gestureDetector.onTouchEvent(event)) {}
      else if (event.getAction == MotionEvent.ACTION_UP) {
        revealView.ripple(event.getX, event.getY)
        delay(500) { EventBus.getDefault.post(Events.FriendCardClicked(friend)) }
      }
      true
    })
  }

  def cardName: String = friend.name

  def update(friend: Friend)

  protected def friend_(friend: Friend): Unit = this.friend = friend

  protected def fetchLatestMessage(): Unit = Try { // use Try since possible NPE
    val latestMsgTextView = find[TextView](R.id.tv_friend_last_msg)
    friend.latestMsg match {
      case Some(msg) =>
        // add "You:" if user sent the last msg
        latestMsgTextView.setText((if (msg.isSentByUser) "You: " else "") + msg.text)
        // bold if msg hasn't been read
        latestMsgTextView.setTypeface(null, if (!msg.isRead) Typeface.BOLD_ITALIC else Typeface.NORMAL)
        // different color for read/unread
        latestMsgTextView.setTextColor((if (!msg.isRead) R.color.primary else R.color.secondary_text).r2Color)
      case None      =>
        latestMsgTextView.setText("")
    }
  }
}
