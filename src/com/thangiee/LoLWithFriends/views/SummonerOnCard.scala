package com.thangiee.LoLWithFriends.views

import android.content.{Intent, Context}
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import android.widget.{ImageView, TextView}
import com.ruenzuo.messageslistview.models.MessageType._
import com.thangiee.LoLWithFriends.activities.ViewOtherSummonerActivity
import com.thangiee.LoLWithFriends.api.LoLStatus._
import com.thangiee.LoLWithFriends.api.Summoner
import com.thangiee.LoLWithFriends.utils.{DataBaseHandler, SummonerUtils}
import com.thangiee.LoLWithFriends.{MyApp, R}
import it.gmariotti.cardslib.library.internal.{CardExpand, ViewToClickToExpand}
import org.jivesoftware.smack.packet.Presence

class SummonerOnCard(ctx: Context, val summoner: Summoner) extends SummonerBaseCard(ctx, summoner, R.layout.summoner_card) {
  private var view: View = _
  private lazy val nameTextView = view.findViewById(R.id.tv_summoner_name).asInstanceOf[TextView]
  private lazy val statusTextView = view.findViewById(R.id.tv_summoner_status).asInstanceOf[TextView]
  private lazy val iconImageView = view.findViewById(R.id.img_profile_icon).asInstanceOf[ImageView]
  private lazy val lastMsgTextView = view.findViewById(R.id.tv_summoner_last_msg).asInstanceOf[TextView]
  private lazy val infoImageView = view.findViewById(R.id.img_info).asInstanceOf[ImageView]
  addCardExpand(new SummonerCardExpand())

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    this.view = view
    nameTextView.setText(summoner.name)

    // load profile icon
    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
    if (prefs.getBoolean(ctx.getResources.getString(R.string.pref_load_icon), true))
      SummonerUtils.loadIconInto(ctx, summoner.name, iconImageView)

    setViewToClickToExpand(ViewToClickToExpand.builder().highlightView(true).setupView(infoImageView))
    refreshCard()
  }

  override def getType: Int = 0

  override def refreshCard(): Unit = {
    if (view != null) {
      updateLastMessage()
      updateStatus()
    }
  }

  private def updateLastMessage() {
    // set last message
    val lastMsg = DataBaseHandler.getLastMessage(MyApp.currentUser, summoner.name)
    lastMsg match {
      case Some(msg) => lastMsgTextView.setText((if(msg.getType.equals(MESSAGE_TYPE_SENT)) "You: " else "") + msg.getText) // add "You:" if user sent the last msg
                        lastMsgTextView.setTypeface(null, if(!msg.isRead) Typeface.BOLD_ITALIC else Typeface.NORMAL) // bold if msg hasn't been read
                        lastMsgTextView.setTextColor(ctx.getResources.getColor(if(!msg.isRead) R.color.summoner_card_last_msg_unread else  R.color.summoner_card_last_msg)) // different color for read/unread
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
    statusTextView.setTextColor(ctx.getResources.getColor(R.color.status_available))
  }

  private def changeToAway() {
    statusTextView.setText("Away")
    statusTextView.setTextColor(ctx.getResources.getColor(R.color.status_away))
  }

  private def changeToBusy() {
    val status = parse(summoner, GameStatus).getOrElse("")
    status match {
      case "inGame"         => val gameTime = (System.currentTimeMillis() - parse(summoner, TimeStamp).get.toLong) / 60000
                               statusTextView.setText("In Game as: "+parse(summoner, SkinName).getOrElse("???")+" (" + Math.round(gameTime)+" mins)")
      case "championSelect" => statusTextView.setText("Champion Selection")
      case "inQueue"        => statusTextView.setText("In Queue")
      case _                => statusTextView.setText(status)
    }
    statusTextView.setTextColor(ctx.getResources.getColor(R.color.status_busy))
  }

  /**
   *  ====================
   *      INNER CLASS
   *  ====================
   */
  private class SummonerCardExpand extends CardExpand(ctx, R.layout.summoner_card_expand) {
    override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
      val levelTextView = view.findViewById(R.id.tv_level).asInstanceOf[TextView]
      val statusMsgTextView = view.findViewById(R.id.tv_status_msg).asInstanceOf[TextView]
      val rankTextView = view.findViewById(R.id.tv_rank_tier).asInstanceOf[TextView]
      val leagueTextView = view.findViewById(R.id.tv_league_name).asInstanceOf[TextView]
      val winTextView = view.findViewById(R.id.tv_wins).asInstanceOf[TextView]
      val badgeImageView = view.findViewById(R.id.img_badge).asInstanceOf[ImageView]

      // set additional summoner infomations
      levelTextView.setText("Level " + parse(summoner, Level).getOrElse("0"))
      statusMsgTextView.setText(parse(summoner, StatusMsg).getOrElse("No Status Message"))
      rankTextView.setText(parse(summoner, RankedLeagueTier).getOrElse("UNRANKED") + " " + parse(summoner, RankedLeagueDivision).getOrElse(""))
      leagueTextView.setText(parse(summoner, RankedLeagueName).getOrElse("NO LEAGUE"))
      winTextView.setText(parse(summoner, Wins).getOrElse("0") + " wins")

      view.findViewById(R.id.bbb).asInstanceOf[TextView].setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = ctx.startActivity(
          new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", summoner.name)
        )
      })

      // set summoner rank badge
      parse(summoner, RankedLeagueTier).getOrElse("") match {
        case "BRONZE"       => badgeImageView.setImageResource(R.drawable.badge_bronze)
        case "SILVER"       => badgeImageView.setImageResource(R.drawable.badge_silver)
        case "GOLD"         => badgeImageView.setImageResource(R.drawable.badge_gold)
        case "PLATINUM"     => badgeImageView.setImageResource(R.drawable.badge_platinum)
        case "CHALLENGER"   => badgeImageView.setImageResource(R.drawable.badge_challenger)
        case _              => badgeImageView.setImageResource(R.drawable.badge_unranked)
      }
    }
  }
}
