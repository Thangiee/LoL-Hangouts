package com.thangiee.LoLHangouts.views

import android.content.Context
import android.graphics.Typeface
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.repository.datasources.sqlite.DB
import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.utils._

case class FriendOffCard(friend: Friend)(implicit ctx: Context) extends FriendBaseCard(friend, R.layout.friend_off_card) {
  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    view.findViewById(R.id.tv_friend_name).asInstanceOf[TextView].setText(friend.name)
    val lastMsgTextView = view.findViewById(R.id.tv_friend_last_msg).asInstanceOf[TextView]

    val lastMsg = DB.getLastMessage(appCtx.currentUser, friend.name)
    lastMsg match {
      case Some(msg) =>
        lastMsgTextView.setText((if (msg.isSentByUser) "You: " else "") + msg.text) // add "You:" if user sent the last msg
        lastMsgTextView.setTypeface(null, if (!msg.isRead) Typeface.BOLD else Typeface.NORMAL) // bold if msg hasn't been read
      case None =>
        lastMsgTextView.setText("")
    }
  }

  override def getType: Int = 1

  override def refreshCard(): Unit = {}
}
