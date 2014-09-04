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
import com.thangiee.LoLHangouts.fragments.LiveGameTeamFragment.{BLUE_TEAM, PURPLE_TEAM}
import com.thangiee.LoLHangouts.utils.{ChampIconAssetFile, ExtractorImplicits, SummonerSpellAssetFile}
import de.greenrobot.event.EventBus

import scala.collection.JavaConversions._

class LiveGameTeamFragment extends TFragment with ExtractorImplicits {
  private lazy val teamListView = find[ListView](R.id.listView)
  private lazy val region = getArguments.getString("region-key")
  private lazy val teamColor = getArguments.getInt("team-key")

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    val players = LiveGameTeamFragment.getTeam(teamColor)
    val preMadeTeams = players.filter(p ⇒ p.teamId.isDefined).map(p ⇒ p.teamId.get).distinct.zipWithIndex.toMap
    view = inflater.inflate(R.layout.live_game_team_view, container, false)
    setupListView(players, preMadeTeams)
    view
  }

  private def setupListView(players: List[LiveGamePlayerStats], preMadeTeams: Map[Int, Int]): Unit = {
    val playerDictionary = new BindDictionary[LiveGamePlayerStats]()

    // load champion icon
    playerDictionary.addStaticImageField(R.id.img_live_game_champ, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        img.setImageDrawable(ChampIconAssetFile(p.chosenChampName).toDrawable)
    })

    playerDictionary.addStaticImageField(R.id.img_live_game_spell1, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        img.setImageDrawable(SummonerSpellAssetFile(p.spellOne.name).toDrawable)
    })

    playerDictionary.addStaticImageField(R.id.img_live_game_spell2, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        img.setImageDrawable(SummonerSpellAssetFile(p.spellTwo.name).toDrawable)
    })

    playerDictionary.addStaticImageField(R.id.img_live_game_pre_made, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        preMadeTeams.get(p.teamId.getOrElse(-1)) match {
          case Some(teamIndex) ⇒
            if      (teamIndex == 0 && teamColor == BLUE_TEAM) img.setImageResource(R.drawable.ic_action_users_light_blue)
            else if (teamIndex == 1 && teamColor == BLUE_TEAM) img.setImageResource(R.drawable.ic_action_users_dark_blue)
            else if (teamIndex == 0 && teamColor == PURPLE_TEAM) img.setImageResource(R.drawable.ic_action_users_light_purp)
            else if (teamIndex == 1 && teamColor == PURPLE_TEAM) img.setImageResource(R.drawable.ic_action_users_dark_purp)
          case None ⇒ img.setImageResource(android.R.color.transparent)
        }
    })

    // load season 4 badge
    playerDictionary.addStaticImageField(R.id.img_s4_badge, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setBadgeDrawable(player.leagueTier, img)
    })

    // player's name with color base on which team
    playerDictionary.addStringField(R.id.tv_live_game_name, (player: LiveGamePlayerStats) ⇒ player.name)
      .conditionalTextColor((player: LiveGamePlayerStats) ⇒ teamColor == BLUE_TEAM, android.R.color.holo_blue_dark.r2Color, android.R.color.holo_purple.r2Color)

    // populate other stats fields
    playerDictionary.addStringField(R.id.tv_live_game_elo, (player: LiveGamePlayerStats) ⇒ player.elo.toString)
    playerDictionary.addStringField(R.id.tv_live_game_s4_leag_info, (player: LiveGamePlayerStats) ⇒ player.leagueTier + " " + player.leagueDivision)
    playerDictionary.addStringField(R.id.tv_live_game_s4_leag_lp, (player: LiveGamePlayerStats) ⇒ player.leaguePoints)
    playerDictionary.addStringField(R.id.tv_live_game_normal_w, (player: LiveGamePlayerStats) ⇒ player.normal5v5.wins.toString + " W")
    playerDictionary.addStringField(R.id.tv_live_game_rank_w, (player: LiveGamePlayerStats) ⇒ player.soloQueue.wins.toString + " W")
    playerDictionary.addStringField(R.id.tv_live_game_rank_l, (player: LiveGamePlayerStats) ⇒ player.soloQueue.losses.toString + " L")
    playerDictionary.addStringField(R.id.tv_live_game_rank_k, (player: LiveGamePlayerStats) ⇒ "%.1f".format(player.soloQueue.kills / player.soloQueue.gameTotal))
    playerDictionary.addStringField(R.id.tv_live_game_rank_d, (player: LiveGamePlayerStats) ⇒ "%.1f".format(player.soloQueue.deaths / player.soloQueue.gameTotal))
    playerDictionary.addStringField(R.id.tv_live_game_rank_a, (player: LiveGamePlayerStats) ⇒ "%.1f".format(player.soloQueue.assists / player.soloQueue.gameTotal))

    // setup button to view player profile
    playerDictionary.addStringField(R.id.btn_live_game_profile, (player: LiveGamePlayerStats) ⇒ "View Profile")
      .onClick((player: LiveGamePlayerStats) ⇒ {
      val i = new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", player.name).putExtra("region-key", region)
      startActivity(i)
    })

    // setup the series images
    List(R.id.img_live_game_serie_1, R.id.img_live_game_serie_2, R.id.img_live_game_serie_3,
      R.id.img_live_game_serie_4, R.id.img_live_game_serie_5).zipWithIndex.foreach { case (id, index) ⇒
      playerDictionary.addStaticImageField(id, new StaticImageLoader[LiveGamePlayerStats] {
        override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
          if (player.series.isDefined) {  // has active series
            val result = player.series.get.result
            if (result.size == 3 && index < 3) setSeriesImgRes(img, result(index))  // 3 games series
            else if (result.size == 5)         setSeriesImgRes(img, result(index))  // 5 games series
          }
          else img.setVisibility(View.INVISIBLE)  // no active series
      })
    }

    val adapter = new FunDapter[LiveGamePlayerStats](ctx, players, R.layout.live_game_player_view2, playerDictionary)
    adapter.setAlternatingBackground(R.color.my_dark_blue, R.color.my_dark_blue2)
    teamListView.setAdapter(adapter)
  }

  private def setBadgeDrawable(tier: String, img: ImageView): Unit = {
    tier.toUpperCase match {
      case "BRONZE" ⇒ img.setImageResource(R.drawable.badge_bronze)
      case "SILVER" ⇒ img.setImageResource(R.drawable.badge_silver)
      case "GOLD" ⇒ img.setImageResource(R.drawable.badge_gold)
      case "DIAMOND" ⇒ img.setImageResource(R.drawable.badge_diamond)
      case "PLATINUM" ⇒ img.setImageResource(R.drawable.badge_platinum)
      case "CHALLENGER" ⇒ img.setImageResource(R.drawable.badge_challenger)
      case _ ⇒ img.setImageResource(R.drawable.badge_unranked)
    }
  }

  private def setSeriesImgRes(imgView: ImageView, result: Int): Unit = {
    imgView.setVisibility(View.VISIBLE)
    if      (result == 1)   imgView.setImageResource(R.color.light_green)  // win result
    else if (result == -1)  imgView.setImageResource(R.color.light_red)    // lose result
    else                    imgView.setImageResource(android.R.color.transparent) // no result yet
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
