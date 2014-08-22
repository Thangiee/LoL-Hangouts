package com.thangiee.LoLHangouts.views

import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.api.Summoner
import com.thangiee.LoLHangouts.utils.Events
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener
import it.gmariotti.cardslib.library.view.CardView

abstract class SummonerBaseCard(ctx: Context, val summ: Summoner, layoutId: Int) extends Card(ctx, layoutId) with TView[CardView] with OnCardClickListener {
  setOnClickListener(this)

  override def basis: CardView = getCardView

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(new Events.SummonerCardClicked(summ))
  }

  def cardName: String = summ.name

  def refreshCard()
 }
