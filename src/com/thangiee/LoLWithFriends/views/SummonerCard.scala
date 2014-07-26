package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.graphics.Typeface
import android.view.{View, ViewGroup}
import android.widget.{ImageView, TextView}
import com.nostra13.universalimageloader.core.ImageLoader
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.api.{LoLStatus, Summoner}
import com.thangiee.LoLWithFriends.utils.{DataBaseHandler, Events, SummonerUtils}
import com.thangiee.LoLWithFriends.{MyApp, R}
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.Card
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener

class SummonerCard(ctx: Context, val summoner: Summoner) extends Card(ctx, R.layout.summoner_card) with OnCardClickListener {
  setOnClickListener(this)

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    val nameTextView = view.findViewById(R.id.tv_summoner_name).asInstanceOf[TextView]
    val statusTextView = view.findViewById(R.id.tv_summoner_status).asInstanceOf[TextView]
    val iconImageView = view.findViewById(R.id.img_profile_icon).asInstanceOf[ImageView]
    val lastMsgTextView = view.findViewById(R.id.tv_summoner_last_msg).asInstanceOf[TextView]

    nameTextView.setText(summoner.name)
    statusTextView.setText(LoLStatus.get(summoner, LoLStatus.StatusMsg))
    // set profile icon
    ImageLoader.getInstance().displayImage(SummonerUtils.profileIconUrl(summoner.name, MyApp.selectedServer), iconImageView)
    // set last message
    val lastMsg = DataBaseHandler.getLastMessage(MyApp.currentUser, summoner.name)
    lastMsg match {
      case Some(msg) => lastMsgTextView.setText((if(msg.getType == MESSAGE_TYPE_SENT) "You: " else "") + msg.getText) // add "You:" if user sent the last msg
                        lastMsgTextView.setTypeface(null, if(!msg.isRead) Typeface.BOLD else Typeface.NORMAL) // bold if msg hasn't been read
      case None      => lastMsgTextView.setText("No active chat")
    }
  }

  override def onClick(p1: Card, p2: View): Unit = {
    EventBus.getDefault.post(new Events.SummonerCardClicked(summoner))
  }

  override def getType: Int = 0
}
