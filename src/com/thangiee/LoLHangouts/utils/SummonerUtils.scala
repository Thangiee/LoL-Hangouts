package com.thangiee.LoLHangouts.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.{R, MyApp}

object SummonerUtils {

  def loadIconInto(ctx: Context, name: String, imageView: ImageView, placeholder: Int = R.drawable.league_icon): Unit = {
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name))
      .placeholder(placeholder)
      .error(R.drawable.load_error)
      .into(imageView)
  }

  def profileIconUrl(name: String): String = {
    "http://avatar.leagueoflegends.com/" + MyApp.selectedServer + "/" + name + ".png"
  }
}
