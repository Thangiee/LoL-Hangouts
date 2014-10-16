package com.thangiee.LoLHangouts.views

import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.api.core.Friend
import com.thangiee.LoLHangouts.utils.Events
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener
import it.gmariotti.cardslib.library.view.CardView

abstract class FriendBaseCard(val f: Friend, layoutId: Int)(implicit ctx: Context) extends Card(ctx, layoutId) with TView[CardView] with OnCardClickListener {
  setOnClickListener(this)

  override def basis: CardView = getCardView

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(Events.FriendCardClicked(f))
  }

  def cardName: String = f.name

  def refreshCard()
 }
