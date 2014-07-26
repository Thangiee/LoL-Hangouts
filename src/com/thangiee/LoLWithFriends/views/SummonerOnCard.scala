package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.graphics.Typeface
import android.view.{View, ViewGroup}
import android.widget.{ImageView, TextView}
import com.nostra13.universalimageloader.core.ImageLoader
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.{SummonerUtils, DataBaseHandler}
import com.thangiee.LoLWithFriends.{MyApp, R}
import org.jivesoftware.smack.packet.Presence

class SummonerOnCard(ctx: Context, val summoner: Summoner) extends SummonerBaseCard(ctx, summoner, R.layout.summoner_card) {
  private var view: View = _
  private lazy val nameTextView = view.findViewById(R.id.tv_summoner_name).asInstanceOf[TextView]
  private lazy val statusTextView = view.findViewById(R.id.tv_summoner_status).asInstanceOf[TextView]
  private lazy val iconImageView = view.findViewById(R.id.img_profile_icon).asInstanceOf[ImageView]
  private lazy val lastMsgTextView = view.findViewById(R.id.tv_summoner_last_msg).asInstanceOf[TextView]

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    this.view = view

    nameTextView.setText(summoner.name)
    refreshCard()
  }

  override def getType: Int = 0

  override def refreshCard(): Unit = {
    // set profile icon
    ImageLoader.getInstance().displayImage(SummonerUtils.profileIconUrl(summoner.name, MyApp.selectedServer), iconImageView)
    updateLastMessage()
    updateStatus()
  }

  private def updateLastMessage() {
    // set last message
    val lastMsg = DataBaseHandler.getLastMessage(MyApp.currentUser, summoner.name)
    lastMsg match {
      case Some(msg) => lastMsgTextView.setText((if(msg.getType == MESSAGE_TYPE_SENT) "You: " else "") + msg.getText) // add "You:" if user sent the last msg
        lastMsgTextView.setTypeface(null, if(!msg.isRead) Typeface.BOLD else Typeface.NORMAL) // bold if msg hasn't been read
      case None      => lastMsgTextView.setText("")
    }
  }

  private def updateStatus() {
    summoner.chatMode match {
      case Presence.Mode.chat => changeToOnline()
      case Presence.Mode.dnd  => changeToBusy()
      case Presence.Mode.away => changeToAway()
      case _ =>
    }
  }

  private def changeToOnline() {
    statusTextView.setText("Online")
  }

  private def changeToAway() {
    statusTextView.setText("Away")
  }

  private def changeToBusy() {
    statusTextView.setText("Busy")
  }
}
