package com.thangiee.LoLHangouts.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SummonerUtils {

  def loadProfileIcon(name: String, regionId: String, imageView: ImageView)(implicit ctx: Context): Unit = {
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, regionId))
      .placeholder(R.drawable.ic_load_unknown)
      .into(imageView)
  }

  def loadProfileIcon(name: String, regionId: String, imageView: ImageView, sideDip: Int)(implicit ctx: Context): Unit = {
    val m = ctx.getResources.getDisplayMetrics
    val s = (sideDip * m.density).toInt
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, regionId))
      .placeholder(R.drawable.ic_load_unknown)
      .resize(s, s)
      .centerCrop()
      .into(imageView)
  }

  def getProfileIcon(name: String, regionId: String, sideDip: Int)(implicit ctx: Context): Future[Drawable] = Future {
    val m = ctx.getResources.getDisplayMetrics
    val s = (sideDip * m.density).toInt
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(name, regionId))
      .placeholder(R.drawable.ic_load_unknown)
      .resize(s, s)
      .centerCrop()
      .get()
      .toDrawable
  }

  def profileIconUrl(name: String, regionId: String): String = {
    s"http://avatar.leagueoflegends.com/$regionId/$name.png"
  }
}
