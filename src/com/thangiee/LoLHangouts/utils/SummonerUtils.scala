package com.thangiee.LoLHangouts.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R

object SummonerUtils {

  def loadIconInto(ctx: Context, name: String, region: String, imageView: ImageView, placeholder: Int = R.drawable.league_icon): Unit = {
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, region))
      .placeholder(placeholder)
      .error(R.drawable.ic_load_error)
      .into(imageView)
  }

  def profileIconUrl(name: String, region: String): String = {
    "http://avatar.leagueoflegends.com/" + region + "/" + name + ".png"
  }
}
