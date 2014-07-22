package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Summoner
import it.gmariotti.cardslib.library.internal.Card

class SummonerCard(ctx: Context, val summoner: Summoner) extends Card(ctx, R.layout.summoner_card) {
  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    val name = view.findViewById(R.id.sum_name).asInstanceOf[TextView]
    name.setText(summoner.name)
  }
}
