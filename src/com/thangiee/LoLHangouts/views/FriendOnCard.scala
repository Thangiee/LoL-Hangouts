package com.thangiee.LoLHangouts.views

import android.content.{Intent, Context}
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.view.{View, ViewGroup}
import android.widget.{ImageButton, ImageView, TextView}
import com.ruenzuo.messageslistview.models.MessageType._
import com.sakout.fancybuttons.FancyButton
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.ViewOtherSummonerActivity
import com.thangiee.LoLHangouts.api.core.Friend
import com.thangiee.LoLHangouts.api.core.LoLStatus._
import com.thangiee.LoLHangouts.utils.{DB, SummonerUtils}
import it.gmariotti.cardslib.library.internal.Card.{OnCollapseAnimatorEndListener, OnExpandAnimatorStartListener}
import it.gmariotti.cardslib.library.internal.{Card, CardExpand, ViewToClickToExpand}
import org.jivesoftware.smack.packet.Presence.Mode

class FriendOnCard(val friend: Friend)(implicit ctx: Context) extends FriendBaseCard(ctx, friend, R.layout.friend_card) {
  private lazy val nameTextView = find[TextView](R.id.tv_friend_name)
  private lazy val statusTextView = find[TextView](R.id.tv_friend_status)
  private lazy val iconImageView = find[ImageView](R.id.img_profile_icon)
  private lazy val lastMsgTextView = find[TextView](R.id.tv_friend_last_msg)
  private lazy val infoButton = find[ImageView](R.id.img_info)
  private lazy val notifyButton = find[ImageButton](R.id.img_bell)
  addCardExpand(new SummonerCardExpand())

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    nameTextView.setText(friend.name)

    // load profile icon
    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
    if (prefs.getBoolean(ctx.getResources.getString(R.string.pref_load_icon), true))
      SummonerUtils.loadProfileIcon(friend.name, appCtx.selectedRegion.id, iconImageView, 55)

    notifyButton.setVisibility(if (friend.chatMode == Mode.chat) View.INVISIBLE else View.VISIBLE)
    notifyButton.setSelected(appCtx.FriendsToNotifyOnAvailable.contains(friend.name))
    notifyButton.setOnClickListener((v: View) ⇒ notifyButtonOnClick())

    setViewToClickToExpand(ViewToClickToExpand.builder().highlightView(true).setupView(infoButton))
    refreshCard()
  }

  private def notifyButtonOnClick(): Unit = {
    notifyButton.setSelected(!notifyButton.isSelected) // Set the button's appearance
    if (notifyButton.isSelected) {
      appCtx.FriendsToNotifyOnAvailable.add(friend.name)
    } else {
      appCtx.FriendsToNotifyOnAvailable.remove(friend.name)
    }
  }

  override def getType: Int = 0

  override def refreshCard(): Unit = {
    if (getCardView != null) {
      updateLastMessage()
      updateStatus()
    }
  }

  private def updateLastMessage() {
    // set last message
    val lastMsg = DB.getLastMessage(appCtx.currentUser, friend.name)
    lastMsg match {
      case Some(msg) => lastMsgTextView.setText((if(msg.getType.equals(MESSAGE_TYPE_SENT)) "You: " else "") + msg.getText) // add "You:" if user sent the last msg
                        lastMsgTextView.setTypeface(null, if(!msg.isRead) Typeface.BOLD_ITALIC else Typeface.NORMAL) // bold if msg hasn't been read
                        lastMsgTextView.setTextColor(ctx.getResources.getColor(if(!msg.isRead) R.color.friend_card_last_msg_unread else  R.color.friend_card_last_msg)) // different color for read/unread
      case None      => lastMsgTextView.setText("")
    }
  }

  private def updateStatus() {
    friend.chatMode match {
      case Mode.chat => changeToOnline()
      case Mode.dnd  => changeToBusy()
      case Mode.away => changeToAway()
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
    val status = parse(friend, GameStatus).getOrElse("")
    status match {
      case "inGame"         => val gameTime = (System.currentTimeMillis() - parse(friend, TimeStamp).get.toLong) / 60000
                               statusTextView.setText("In Game: " + parse(friend, SkinName).getOrElse("???")+" (" + Math.round(gameTime)+" mins)")
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
  private class SummonerCardExpand extends CardExpand(ctx, R.layout.friend_card_expand) {
    override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
      val levelTextView = view.findViewById(R.id.tv_level).asInstanceOf[TextView]
      val statusMsgTextView = view.findViewById(R.id.tv_status_msg).asInstanceOf[TextView]
      val rankTextView = view.findViewById(R.id.tv_rank_tier).asInstanceOf[TextView]
      val leagueTextView = view.findViewById(R.id.tv_league_name).asInstanceOf[TextView]
      val winTextView = view.findViewById(R.id.tv_wins).asInstanceOf[TextView]
      val badgeImageView = view.findViewById(R.id.img_badge).asInstanceOf[ImageView]

      // set additional summoner infomations
      levelTextView.setText("Level " + parse(friend, Level).getOrElse("0"))
      statusMsgTextView.setText(parse(friend, StatusMsg).getOrElse("No Status Message"))
      rankTextView.setText(parse(friend, RankedLeagueTier).getOrElse("UNRANKED") + " " + parse(friend, RankedLeagueDivision).getOrElse(""))
      leagueTextView.setText(parse(friend, RankedLeagueName).getOrElse("NO LEAGUE"))
      winTextView.setText(parse(friend, Wins).getOrElse("0") + " wins")

      find[FancyButton](R.id.btn_view_profile).setOnClickListener((v: View) ⇒
        new Intent(ctx, classOf[ViewOtherSummonerActivity])
          .putExtra("name-key", friend.name)
          .putExtra("region-key", appCtx.selectedRegion.id))

      // set summoner rank badge
      parse(friend, RankedLeagueTier).getOrElse("") match {
        case "BRONZE"       ⇒ badgeImageView.setImageResource(R.drawable.badge_bronze)
        case "SILVER"       ⇒ badgeImageView.setImageResource(R.drawable.badge_silver)
        case "GOLD"         ⇒ badgeImageView.setImageResource(R.drawable.badge_gold)
        case "PLATINUM"     ⇒ badgeImageView.setImageResource(R.drawable.badge_platinum)
        case "DIAMOND"      ⇒  badgeImageView.setImageResource(R.drawable.badge_diamond)
        case "MASTER"       ⇒ badgeImageView.setImageResource(R.drawable.badge_master)
        case "CHALLENGER"   ⇒ badgeImageView.setImageResource(R.drawable.badge_challenger)
        case _              ⇒ badgeImageView.setImageResource(R.drawable.badge_unranked)
      }

      setOnExpandAnimatorStartListener(new OnExpandAnimatorStartListener {
        override def onExpandStart(p1: Card): Unit = {}
      })

      setOnCollapseAnimatorEndListener(new OnCollapseAnimatorEndListener {
        override def onCollapseEnd(p1: Card): Unit = {}
      })
    }
  }
}
