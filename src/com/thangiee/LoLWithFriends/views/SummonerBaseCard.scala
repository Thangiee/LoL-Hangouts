package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.view.View
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener

abstract class SummonerBaseCard(ctx: Context, val summ: Summoner, layoutId: Int) extends Card(ctx, layoutId) with OnCardClickListener {
  setOnClickListener(this)

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(new Events.SummonerCardClicked(summ))
  }

  def cardName: String = summ.name

  def refreshCard()
}
