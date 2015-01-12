package com.thangiee.LoLHangouts.views

import android.content.Context
import android.view.{View, ViewGroup}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.domain.entities.Friend

case class FriendOffCard(private var friend: Friend)(implicit ctx: Context) extends FriendBaseCard(friend, R.layout.friend_off_card) {

  override def getType: Int = 1

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    super.setupInnerViewElements(parent, view)
    fetchLatestMessage()
  }

  override def update(friend: Friend): Unit = {
    this.friend = friend
    super.friend_(friend)
    fetchLatestMessage()
  }
}
