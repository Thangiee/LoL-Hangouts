package com.thangiee.LoLHangouts.views

import android.content.Context
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.view.{View, ViewGroup}
import android.widget.{ImageButton, ImageView, TextView}
import com.sakout.fancybuttons.FancyButton
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{ViewLiveGameStatsActivity, ViewOtherSummonerActivity}
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.data.repository.datasources.net.core.LoLChat
import com.thangiee.LoLHangouts.data.repository.datasources.sqlite.DB
import com.thangiee.LoLHangouts.domain.entities.{ChatMode, Friend}
import com.thangiee.LoLHangouts.utils.Logger._
import com.thangiee.LoLHangouts.utils._
import it.gmariotti.cardslib.library.internal.Card.{OnCollapseAnimatorEndListener, OnExpandAnimatorStartListener}
import it.gmariotti.cardslib.library.internal.{Card, CardExpand, ViewToClickToExpand}

case class FriendOnCard(friend: Friend)(implicit ctx: Context) extends FriendBaseCard(friend, R.layout.friend_card) {
  private lazy val nameTextView    = find[TextView](R.id.tv_friend_name)
  private lazy val statusTextView  = find[TextView](R.id.tv_friend_status)
  private lazy val iconImageView   = find[ImageView](R.id.img_profile_icon)
  private lazy val lastMsgTextView = find[TextView](R.id.tv_friend_last_msg)
  private lazy val infoButton      = find[ImageView](R.id.img_info)
  private lazy val notifyButton    = find[ImageButton](R.id.img_bell)
  addCardExpand(new SummonerCardExpand())

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    nameTextView.setText(friend.name)

    // load profile icon
    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
    if (prefs.getBoolean(ctx.getResources.getString(R.string.pref_load_icon), true))
      SummonerUtils.loadProfileIcon(friend.name, PrefsCache.getString(CacheKey.LoginRegionId).get, iconImageView, 55)

    notifyButton.setVisibility(if (friend.chatMode == ChatMode.Chat) View.INVISIBLE else View.VISIBLE)
    notifyButton.setSelected(appCtx.FriendsToNotifyOnAvailable.contains(friend.name))
    notifyButton.onClick(notifyButtonOnClick())

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
    DB.getLastMessage(appCtx.currentUser, friend.name) match {
      case Some(msg) =>
        lastMsgTextView.setText((if (msg.isSentByUser) "You: " else "") + msg.text) // add "You:" if user sent the last msg
        lastMsgTextView.setTypeface(null, if (!msg.isRead) Typeface.BOLD_ITALIC else Typeface.NORMAL) // bold if msg hasn't been read
        lastMsgTextView.setTextColor(ctx.getResources.getColor(if (!msg.isRead) R.color.friend_card_last_msg_unread else R.color.friend_card_last_msg)) // different color for read/unread
      case None =>
        lastMsgTextView.setText("")
    }
  }

  private def updateStatus() {
    friend.chatMode match {
      case ChatMode.Chat => changeToOnline()
      case ChatMode.Dnd  => changeToBusy()
      case ChatMode.Away => changeToAway()
      case _ => warn("[!] No chat mode match")
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
    friend.gameStatus match {
      case "inGame"         => val gameTime = friend.timeInGame / 60000
                               statusTextView.setText(s"In Game: ${friend.championSelect.getOrElse("???")} (${Math.round(gameTime)} mins)")
      case "championSelect" => statusTextView.setText("Champion Selection")
      case "inQueue"        => statusTextView.setText("In Queue")
      case other: String    => statusTextView.setText(other)
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
      levelTextView.setText("Level " + friend.level)
      statusMsgTextView.setText(friend.statusMsg)
      rankTextView.setText(s"${friend.rankedLeagueTier} ${friend.rankedLeagueDivision}")
      leagueTextView.setText(friend.rankedLeagueName)
      winTextView.setText(friend.wins + " wins")

      find[FancyButton](R.id.btn_view_profile)
        .onClick(ctx.startActivity(ViewOtherSummonerActivity(friend.name, appCtx.selectedRegion.id)))

      find[FancyButton](R.id.btn_live_game)
        .onClick(ctx.startActivity(ViewLiveGameStatsActivity(friend.name, appCtx.selectedRegion.id)))

      find[FancyButton](R.id.btn_remove_friends).onClick(ConfirmDialog(
        msg = s"You are about to REMOVE\n ${friend.name}",
        code2run = LoLChat.connection.getRoster.removeEntry(LoLChat.getFriendByName(friend.name).get.entry), // todo: temporary
        btnTitle = "Remove"
      ).show())

      // set summoner rank badge
      friend.rankedLeagueTier match {
        case "BRONZE"     => badgeImageView.setImageResource(R.drawable.badge_bronze)
        case "SILVER"     => badgeImageView.setImageResource(R.drawable.badge_silver)
        case "GOLD"       => badgeImageView.setImageResource(R.drawable.badge_gold)
        case "PLATINUM"   => badgeImageView.setImageResource(R.drawable.badge_platinum)
        case "DIAMOND"    => badgeImageView.setImageResource(R.drawable.badge_diamond)
        case "MASTER"     => badgeImageView.setImageResource(R.drawable.badge_master)
        case "CHALLENGER" => badgeImageView.setImageResource(R.drawable.badge_challenger)
        case _            => badgeImageView.setImageResource(R.drawable.badge_unranked)
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
