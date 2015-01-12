package com.thangiee.LoLHangouts.views

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.domain.entities.Friend

case class FriendOffCard(private var friend: Friend)(implicit ctx: Context) extends FriendBaseCard(friend, R.layout.friend_off_card) {

  lazy val nameTextView = find[TextView](R.id.tv_friend_name)
  lazy val latestMsgTextView = find[TextView](R.id.tv_friend_last_msg)

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    super.setupInnerViewElements(parent, view)
    fetchLatestMessage()
  }

  override def getType: Int = 1

  override def update(friend: Friend): Unit = {
    this.friend = friend
    super.friend_(friend)
  }

}
