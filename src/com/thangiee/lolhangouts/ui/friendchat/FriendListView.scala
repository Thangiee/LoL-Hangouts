package com.thangiee.lolhangouts.ui.friendchat

import android.content.Context
import android.widget.FrameLayout
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.usecases.GetFriendsUseCaseImpl
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.utils._
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._

class FriendListView(implicit ctx: Context) extends FrameLayout(ctx) with CustomView {
  private lazy val cardListView     = find[CardListView](R.id.card_list)
  private      val cards            = scala.collection.mutable.ArrayBuffer[FriendBaseCard]()
  private      val cardArrayAdapter = new CardArrayAdapter(ctx, cards)

  override protected val presenter = new FriendListPresenter(this, GetFriendsUseCaseImpl())

  private var _friendGroupToShow = "all"
  def friendGroupToShow: String = _friendGroupToShow
  def friendGroupToShow_=(groupName: String): Unit = _friendGroupToShow = groupName

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.friend_list_view, this, false))
    cardArrayAdapter.setNotifyOnChange(false)
    cardArrayAdapter.setInnerViewTypeCount(2) // important with different inner layout

    val animationAdapter = new SwingLeftInAnimationAdapter(cardArrayAdapter)
    animationAdapter.setAbsListView(cardListView)
    cardListView.setExternalAdapter(animationAdapter, cardArrayAdapter)
  }

  def initCardList(onFriends: Seq[Friend], offFriends: Seq[Friend]): Unit = {
    cards.clear()
    cards.++=(onFriends.map(f => FriendOnCard(f)))
    cards.++=(offFriends.map(f => FriendOffCard(f)))
    cardArrayAdapter.notifyDataSetChanged()
  }

  def updateCardContent(friend: Friend): Unit = {
    for ( i <- 0 until cardArrayAdapter.getCount ) {
      val baseCard = cardArrayAdapter.getItem(i).asInstanceOf[FriendBaseCard] // get the card view
      if (baseCard.cardName.toLowerCase == friend.name.toLowerCase) {
        info(s"[+] Found ${friend.name} card")
        baseCard.update(friend)
        cardArrayAdapter.notifyDataSetChanged()
        return
      }
    }
    warn(s"[-] No card found for ${friend.name}")
  }
}
