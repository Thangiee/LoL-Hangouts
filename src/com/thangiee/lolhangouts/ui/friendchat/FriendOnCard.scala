package com.thangiee.lolhangouts.ui.friendchat

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.{ImageButton, ImageView, TextView}
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.andexert.library.RippleView
import com.sakout.fancybuttons.FancyButton
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.datasources.net.core.LoLChat
import com.thangiee.lolhangouts.data.usecases.entities.{ChatMode, Friend}
import com.thangiee.lolhangouts.ui.livegame.ViewGameScouterActivity
import com.thangiee.lolhangouts.ui.profile.ViewProfileActivity
import com.thangiee.lolhangouts.ui.utils._
import it.gmariotti.cardslib.library.internal.Card.{OnCollapseAnimatorEndListener, OnExpandAnimatorStartListener}
import it.gmariotti.cardslib.library.internal.{Card, CardExpand, ViewToClickToExpand}

case class FriendOnCard(private var friend: Friend)(implicit ctx: Context) extends FriendBaseCard(friend, R.layout.friend_card) {
  private lazy val statusTextView = find[TextView](R.id.tv_friend_status)
  private lazy val iconImageView  = find[ImageView](R.id.img_profile_icon)
  private lazy val infoButton     = find[ImageButton](R.id.img_info)
  private lazy val notifyButton   = find[RippleView](R.id.img_bell_ripple)
  addCardExpand(new SummonerCardExpand())

  override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
    super.setupInnerViewElements(parent, view)

    // load profile icon
    if (R.string.pref_load_icon.pref2Boolean(default = true))
      SummonerUtils.loadProfileIcon(friend.name, friend.regionId, iconImageView, 55)

    notifyButton.setVisibility(if (friend.chatMode == ChatMode.Chat) View.INVISIBLE else View.VISIBLE)
    notifyButton.setSelected(appCtx.FriendsToNotifyOnAvailable.contains(friend.name))
    notifyButton.onClick(notifyButtonOnClick())

    setViewToClickToExpand(ViewToClickToExpand.builder().highlightView(true).setupView(infoButton))
    fetchLatestMessage()
    updateStatus()
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

  override def update(friend: Friend): Unit = {
    if (getCardView != null) {
      this.friend = friend
      super.friend_(friend)
      fetchLatestMessage()
      updateStatus()
    }
  }

  private def updateStatus() {
    friend.chatMode match {
      case ChatMode.Chat => changeToOnline()
      case ChatMode.Dnd  => changeToBusy()
      case ChatMode.Away => changeToAway()
      case _             => warn("[!] No chat mode match")
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
   * ====================
   * INNER CLASS
   * ====================
   */
  private class SummonerCardExpand extends CardExpand(ctx, R.layout.friend_card_expand) {
    override def setupInnerViewElements(parent: ViewGroup, view: View): Unit = {
      val levelTextView     = view.find[TextView](R.id.tv_level)
      val statusMsgTextView = view.find[TextView](R.id.tv_status_msg)
      val rankTextView      = view.find[TextView](R.id.tv_rank_tier)
      val leagueTextView    = view.find[TextView](R.id.tv_league_name)
      val winTextView       = view.find[TextView](R.id.tv_wins)
      val badgeImageView    = view.find[ImageView](R.id.img_badge)
      val profileBtn        = view.find[FancyButton](R.id.btn_view_profile)
      val liveGameBtn       = view.find[FancyButton](R.id.btn_live_game)
      val removeFriendBtn   = view.find[FancyButton](R.id.btn_remove_friends)

      // set additional summoner infomations
      levelTextView.setText("Level " + friend.level)
      statusMsgTextView.setText(friend.statusMsg)
      rankTextView.setText(s"${friend.rankedLeagueTier} ${friend.rankedLeagueDivision}")
      leagueTextView.setText(friend.rankedLeagueName)
      winTextView.setText(friend.wins + " wins")

      profileBtn.onClick(delay(500) { ctx.startActivity(ViewProfileActivity(friend.name, friend.regionId)) })
      liveGameBtn.onClick(delay(500) { ctx.startActivity(ViewGameScouterActivity(friend.name, friend.regionId)) })

      removeFriendBtn.onClick(delay(500) {
        new Builder(ctx)
          .title("Remove friend?")
          .content(s"You are about to REMOVE ${friend.name} from your friend list!")
          .positiveText("Remove")
          .negativeText("Cancel")
          .onPositive((dialog) => LoLChat.connection.getRoster.removeEntry(LoLChat.getFriendByName(friend.name).get.entry))
          .show()
      })

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
