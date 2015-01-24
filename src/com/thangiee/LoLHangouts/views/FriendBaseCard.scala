package com.thangiee.LoLHangouts.views

import android.content.Context
import android.graphics.Typeface
import android.view.{ViewGroup, View}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.utils.{Events, _}
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener
import it.gmariotti.cardslib.library.view.CardView
import org.scaloid.common.{TagUtil, TraitView}

import scala.util.Try

abstract class FriendBaseCard(private var friend: Friend, layoutId: Int)(implicit ctx: Context) extends Card(ctx, layoutId)
with TraitView[CardView] with OnCardClickListener with TagUtil {

  setOnClickListener(this)

  override def basis: CardView = getCardView

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(Events.FriendCardClicked(friend))
  }

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    super.setupInnerViewElements(parent, view)
    view.find[TextView](R.id.tv_friend_name).setText(friend.name)
  }

  def cardName: String = friend.name

  def update(friend: Friend)

  protected def friend_(friend: Friend): Unit = this.friend = friend

  protected def fetchLatestMessage(): Unit = Try {
    val latestMsgTextView = find[TextView](R.id.tv_friend_last_msg) // use Try since possible NPE
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
