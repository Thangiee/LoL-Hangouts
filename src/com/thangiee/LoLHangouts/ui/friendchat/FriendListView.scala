package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter
import com.thangiee.LoLHangouts.CustomView
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.Friend
import com.thangiee.LoLHangouts.domain.interactor.GetFriendsUseCaseImpl
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.views.{FriendBaseCard, FriendOffCard, FriendOnCard}
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._

class FriendListView(implicit ctx: Context) extends CardListView(ctx) with CustomView {

  override val presenter = new FriendListPresenter(this, GetFriendsUseCaseImpl())

  val cards = scala.collection.mutable.ArrayBuffer[FriendBaseCard]()
  val cardArrayAdapter = new CardArrayAdapter(ctx, cards)

  override def onAttached(): Unit = {
    super.onAttached()
    cardArrayAdapter.setNotifyOnChange(false)
    cardArrayAdapter.setInnerViewTypeCount(2) // important with different inner layout

    val animationAdapter = new AlphaInAnimationAdapter(cardArrayAdapter)
    animationAdapter.setAbsListView(this)
    setExternalAdapter(animationAdapter, cardArrayAdapter)
  }

  def initCardList(onFriends: List[Friend], offFriends: List[Friend]): Unit = {
    cards.clear()
    cards.++=(onFriends.map(f => FriendOnCard(f)))
    cards.++=(offFriends.map(f => FriendOffCard(f)))
    cardArrayAdapter.notifyDataSetChanged()
  }

  def updateCardContent(friend: Friend): Unit = {
    for (i <- 0 until cardArrayAdapter.getCount) {
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

  def showLoading(): Unit = {
    // todo: add loading
  }

  def hideLoading(): Unit ={

  }
}
