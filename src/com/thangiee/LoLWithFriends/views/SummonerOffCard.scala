package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.DataBaseHandler

class SummonerOffCard(ctx: Context, summoner: Summoner) extends SummonerBaseCard(ctx, summoner, R.layout.summoner_off_card) {

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    view.findViewById(R.id.tv_summoner_name).asInstanceOf[TextView].setText(summoner.name)
    val a = view.findViewById(R.id.tv_summoner_last_msg).asInstanceOf[TextView]

    val last = DataBaseHandler.getLastMessage("thangiee", summoner.name)
    last match {
      case Some(m) => a.setText(m.getText)
      case None => a.setText("No active chat")
    }
  }

  override def getType: Int = 1

  override def refreshCard(): Unit = {}
}
