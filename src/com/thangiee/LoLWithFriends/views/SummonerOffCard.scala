package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.graphics.Typeface
import android.view.{View, ViewGroup}
import android.widget.TextView
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.{MyApp, R}
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.DataBaseHandler

class SummonerOffCard(ctx: Context, summoner: Summoner) extends SummonerBaseCard(ctx, summoner, R.layout.summoner_off_card) {

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    view.findViewById(R.id.tv_summoner_name).asInstanceOf[TextView].setText(summoner.name)
    val lastMsgTextView = view.findViewById(R.id.tv_summoner_last_msg).asInstanceOf[TextView]

    val lastMsg = DataBaseHandler.getLastMessage(MyApp.currentUser, summoner.name)
    lastMsg match {
      case Some(msg) => lastMsgTextView.setText((if (msg.getType.equals(MESSAGE_TYPE_SENT)) "You: " else "") + msg.getText) // add "You:" if user sent the last msg
                        lastMsgTextView.setTypeface(null, if (!msg.isRead) Typeface.BOLD else Typeface.NORMAL) // bold if msg hasn't been read
      case None => lastMsgTextView.setText("")
    }
  }

  override def getType: Int = 1

  override def refreshCard(): Unit = {}
}
