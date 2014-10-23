package com.thangiee.LoLHangouts.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R

object SummonerUtils {

  def loadProfileIcon(name: String, region: String, imageView: ImageView)(implicit ctx: Context): Unit = {
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, region))
      .placeholder(R.drawable.ic_load_unknown)
      .into(imageView)
  }

  def loadProfileIcon(name: String, region: String, imageView: ImageView, sideDip: Int)(implicit ctx: Context): Unit = {
    val m = ctx.getResources.getDisplayMetrics
    val s = (sideDip * m.density).toInt
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, region))
      .placeholder(R.drawable.ic_load_unknown)
      .resize(s, s)
      .centerCrop()
      .into(imageView)
  }

  def profileIconUrl(name: String, region: String): String = {
    s"http://avatar.leagueoflegends.com/$region/$name.png"
  }
}
