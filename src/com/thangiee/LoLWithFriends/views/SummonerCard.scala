package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.Events
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener

class SummonerCard(ctx: Context, val summoner: Summoner) extends Card(ctx, R.layout.summoner_card) with OnCardClickListener {
  setOnClickListener(this)

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    val name = view.findViewById(R.id.sum_name).asInstanceOf[TextView]
    name.setText(summoner.name)
  }

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(new Events.SummonerCardClicked(summoner))
  }
}
