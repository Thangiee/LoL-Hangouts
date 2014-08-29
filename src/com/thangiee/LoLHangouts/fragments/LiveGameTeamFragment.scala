package com.thangiee.LoLHangouts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.ViewOtherSummonerActivity
import com.thangiee.LoLHangouts.api.stats.LiveGamePlayerStats
import com.thangiee.LoLHangouts.fragments.LiveGameTeamFragment.BLUE_TEAM
import com.thangiee.LoLHangouts.utils.{ChampIconAssetFile, ExtractorImplicits}
import de.greenrobot.event.EventBus

import scala.collection.JavaConversions._

class LiveGameTeamFragment extends TFragment with ExtractorImplicits {
  private lazy val teamListView = find[ListView](R.id.listView)
  private lazy val region = getArguments.getString("region-key")
  private lazy val teamColor = getArguments.getInt("team-key")

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    val players = LiveGameTeamFragment.getTeam(teamColor)
    view = inflater.inflate(R.layout.live_game_team_view, container, false)
    setupListView(players)
    view
  }

  private def setupListView(players: List[LiveGamePlayerStats]): Unit = {
    val playerDictionary = new BindDictionary[LiveGamePlayerStats]()

    // load champion icon
    playerDictionary.addStaticImageField(R.id.img_live_game_champ, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        img.setImageDrawable(ChampIconAssetFile(p.chosenChamp).toDrawable)
    })

    // load season 4 badge
    playerDictionary.addStaticImageField(R.id.img_s4_badge, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setBadgeDrawable(player.leagueTier, img)
    })

    // player's name with color base on which team
    playerDictionary.addStringField(R.id.tv_live_game_name, (player: LiveGamePlayerStats) ⇒ player.name)
      .conditionalTextColor((player: LiveGamePlayerStats) ⇒ teamColor == BLUE_TEAM, android.R.color.holo_blue_dark.r2Color, android.R.color.holo_purple.r2Color)

    // populate other fields
    playerDictionary.addStringField(R.id.tv_live_game_s4_leag_info, (player: LiveGamePlayerStats) ⇒ leagueInfo(player))
    playerDictionary.addStringField(R.id.tv_live_game_normal_w, (player: LiveGamePlayerStats) ⇒ player.normal5v5.wins.toString + "W")
    playerDictionary.addStringField(R.id.tv_live_game_rank_w_l, (player: LiveGamePlayerStats) ⇒ {
      val soloQueue = player.soloQueue
      soloQueue.wins + "W | " + soloQueue.losses + "L"
    })
    playerDictionary.addStringField(R.id.tv_live_game_kda, (player: LiveGamePlayerStats) ⇒ {
      val soloQueue = player.soloQueue
      val games = soloQueue.gameTotal
      "%.1f/%.1f/%.1f".format(soloQueue.kills / games, soloQueue.deaths / games, soloQueue.assists / games)
    })

    // setup button to view player profile
    playerDictionary.addStringField(R.id.btn_live_game_profile, (player: LiveGamePlayerStats) ⇒ "View Profile")
      .onClick((player: LiveGamePlayerStats) ⇒ {
      val i = new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", player.name).putExtra("region-key", region)
      startActivity(i)
    })

    // load series img if up for promotion
    playerDictionary.addStaticImageField(R.id.img_live_game_series, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setSeriesImgRes(player, img)
    })

    val adapter = new FunDapter[LiveGamePlayerStats](ctx, players, R.layout.live_game_player_view2, playerDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    teamListView.setAdapter(adapter)
  }

  private def leagueInfo(player: LiveGamePlayerStats): String = {
    player.leagueTier + " " + player.leagueDivision + " (" + player.leaguePoints + ")"
  }

  private def setBadgeDrawable(tier: String, img: ImageView): Unit = {
    tier.toUpperCase match {
      case "BRONZE" ⇒ img.setImageResource(R.drawable.badge_bronze)
      case "SILVER" ⇒ img.setImageResource(R.drawable.badge_silver)
      case "GOLD"   ⇒ img.setImageResource(R.drawable.badge_gold)
      case "DIAMOND" ⇒ img.setImageResource(R.drawable.badge_diamond)
      case "PLATINUM" ⇒ img.setImageResource(R.drawable.badge_platinum)
      case "CHALLENGER" ⇒ img.setImageResource(R.drawable.badge_challenger)
      case _ ⇒ img.setImageResource(R.drawable.badge_unranked)
    }
  }

  private def setSeriesImgRes(player: LiveGamePlayerStats, img: ImageView): Unit = {
    player.series match { // todo: find better way to do this...
      case Some(s) ⇒ if (s.result.size == 3) {
        if      (s.result.equals(List(1, -1, 0))) img.setImageResource(R.drawable.series_3_wl)
        else if (s.result.equals(List(-1, 1, 0))) img.setImageResource(R.drawable.series_3_lw)
        else if (s.result.equals(List(1, 0, 0)))  img.setImageResource(R.drawable.series_3_w)
        else if (s.result.equals(List(-1, 0, 0))) img.setImageResource(R.drawable.series_3_l)
        else                                      img.setImageResource(R.drawable.series_3)
      } else {
        if      (s.result.equals(List(1, 1, 0, 0, 0))) img.setImageResource(R.drawable.series_5_ww)
        else if (s.result.equals(List(-1, -1, 0, 0, 0))) img.setImageResource(R.drawable.series_5_ll)
        else if (s.result.equals(List(1, -1, 0, 0, 0))) img.setImageResource(R.drawable.series_5_wl)
        else if (s.result.equals(List(-1, 1, 0, 0, 0))) img.setImageResource(R.drawable.series_5_lw)
        else if (s.result.equals(List(1, 0, 0, 0, 0))) img.setImageResource(R.drawable.series_5_w)
        else if (s.result.equals(List(-1, 0, 0, 0, 0))) img.setImageResource(R.drawable.series_5_l)
        else if (s.result.equals(List(1, 1, -1, 0, 0))) img.setImageResource(R.drawable.series_5_wwl)
        else if (s.result.equals(List(-1, -1, 1, 0, 0))) img.setImageResource(R.drawable.series_5_llw)
        else if (s.result.equals(List(1, -1, 1, 0, 0))) img.setImageResource(R.drawable.series_5_wlw)
        else if (s.result.equals(List(-1, 1, -1, 0, 0))) img.setImageResource(R.drawable.series_5_lwl)
        else if (s.result.equals(List(-1, 1, -1, 1, 0))) img.setImageResource(R.drawable.series_5_lwlw)
        else if (s.result.equals(List(1, -1, 1, -1, 0))) img.setImageResource(R.drawable.series_5_wlwl)
        else if (s.result.equals(List(1, 1, -1, -1, 0))) img.setImageResource(R.drawable.series_5_wwll)
        else if (s.result.equals(List(-1, -1, 1, 1, 0))) img.setImageResource(R.drawable.series_5_llww)
        else                                      img.setImageResource(R.drawable.series_5)
      }
      case None ⇒ img.setImageResource(android.R.color.transparent)
    }
  }
}

object LiveGameTeamFragment {
  val BLUE_TEAM = 0
  val PURPLE_TEAM = 1
  def newInstance(players: List[LiveGamePlayerStats], team: Int, region: String): LiveGameTeamFragment = {
    if (team == BLUE_TEAM)
      EventBus.getDefault.postSticky(new BlueTeam(players))
    else
      EventBus.getDefault.postSticky(new PurpleTeam(players))

    val b = new Bundle()
    b.putInt("team-key", team)
    b.putString("region-key", region)
    val frag = new LiveGameTeamFragment
    frag.setArguments(b)
    frag
  }

  private def getTeam(n: Int): List[LiveGamePlayerStats] = {
    if (n == BLUE_TEAM)
      EventBus.getDefault.removeStickyEvent(classOf[BlueTeam]).team
    else
      EventBus.getDefault.removeStickyEvent(classOf[PurpleTeam]).team
  }

  private case class BlueTeam(team: List[LiveGamePlayerStats])

  private case class PurpleTeam(team: List[LiveGamePlayerStats])
}
