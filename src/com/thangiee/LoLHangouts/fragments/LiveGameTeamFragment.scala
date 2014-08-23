package com.thangiee.LoLHangouts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, ListView}
import com.ami.fundapter.interfaces.{ItemClickListener, StaticImageLoader}
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.ViewOtherSummonerActivity
import com.thangiee.LoLHangouts.api.stats.LiveGamePlayerStats
import com.thangiee.LoLHangouts.utils.ExtractorImplicits
import de.greenrobot.event.EventBus

import scala.collection.JavaConversions._

class LiveGameTeamFragment extends TFragment with ExtractorImplicits {
  private lazy val teamListView = find[ListView](R.id.listView)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    val teamNum = getArguments.getInt("team-key")
    val players = LiveGameTeamFragment.getTeam(teamNum)
    view = inflater.inflate(R.layout.live_game_team_view, container, false)
    setupListView(players)
    view
  }

  private def setupListView(players: List[LiveGamePlayerStats]): Unit = {
    val playerDictionary = new BindDictionary[LiveGamePlayerStats]()

    playerDictionary.addStaticImageField(R.id.img_live_game_champ, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(p: LiveGamePlayerStats, img: ImageView, p3: Int): Unit =
        Picasso.`with`(ctx).load("http://www.mobafire.com/images/champion/icon/" + p.chosenChamp.toLowerCase + ".png")
          .placeholder(R.drawable.league_icon)
          .error(R.drawable.ic_load_error)
          .into(img)
    })

    playerDictionary.addStaticImageField(R.id.img_s4_badge, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setBadgeDrawable(player.leagueTier, img)
    })

    playerDictionary.addStaticImageField(R.id.img_s3_badge, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setBadgeDrawable(player.previousLeagueTier, img)
    })

    // todo: optimize performance
    playerDictionary.addStringField(R.id.tv_live_game_name, (player: LiveGamePlayerStats) ⇒ player.name)
    playerDictionary.addStringField(R.id.tv_live_game_s4_leag_info, (player: LiveGamePlayerStats) ⇒ leagueInfo(player))
    playerDictionary.addStringField(R.id.tv_live_game_s3_leag_info, (player: LiveGamePlayerStats) ⇒ player.previousLeagueTier)
    playerDictionary.addStringField(R.id.tv_live_game_normal_w, (player: LiveGamePlayerStats) ⇒ player.normal5v5.wins.toString + "W")
    playerDictionary.addStringField(R.id.tv_live_game_rank_w_l, (player: LiveGamePlayerStats) ⇒ {
      val soloQueue = player.soloQueue
      soloQueue.wins + "W | " + soloQueue.losses + "L"
    })
    playerDictionary.addStringField(R.id.tv_live_game_kda, (player: LiveGamePlayerStats) ⇒ {
      val soloQueue = player.soloQueue
      soloQueue.kills + "/" + soloQueue.deaths + "/" + soloQueue.assists
    })
    playerDictionary.addStringField(R.id.btn_live_game_profile, (player: LiveGamePlayerStats) ⇒ "View Profile")
      .onClick(new ItemClickListener[LiveGamePlayerStats] {
      override def onClick(player: LiveGamePlayerStats, p2: Int, view: View): Unit = {
        val i = new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", player.name).putExtra("region-key", "na") // todo: change region
        startActivity(i)
      }
    })

    playerDictionary.addStaticImageField(R.id.img_live_game_series, new StaticImageLoader[LiveGamePlayerStats] {
      override def loadImage(player: LiveGamePlayerStats, img: ImageView, p3: Int): Unit = setSeriesDrawable(player, img)
    })

    val adapter = new FunDapter[LiveGamePlayerStats](ctx, players, R.layout.live_game_player_view, playerDictionary)
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

  private def setSeriesDrawable(player: LiveGamePlayerStats, img: ImageView): Unit = {
    player.series match {
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
  def newInstance(players: List[LiveGamePlayerStats], team: Int): LiveGameTeamFragment = {
    // todo: team number
    if (team == 1)
      EventBus.getDefault.postSticky(new TeamOne(players))
    else
      EventBus.getDefault.postSticky(new TeamTwo(players))

    val b = new Bundle()
    b.putInt("team-key", team)
    val frag = new LiveGameTeamFragment
    frag.setArguments(b)
    frag
  }

  private def getTeam(n: Int): List[LiveGamePlayerStats] = {
    if (n == 1)
      EventBus.getDefault.removeStickyEvent(classOf[TeamOne]).team
    else
      EventBus.getDefault.removeStickyEvent(classOf[TeamTwo]).team
  }

  private case class TeamOne(team: List[LiveGamePlayerStats])

  private case class TeamTwo(team: List[LiveGamePlayerStats])

}
