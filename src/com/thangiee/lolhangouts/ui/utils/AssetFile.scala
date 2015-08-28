package com.thangiee.lolhangouts.ui.utils

import android.content.Context
import android.graphics.{Typeface, BitmapFactory, Bitmap}
import android.graphics.drawable.Drawable
import com.thangiee.lolhangouts.R

import scala.util.Try

sealed trait AssetFile {
  protected def path: String
}

sealed trait ImageFile extends AssetFile {
  def toDrawable(implicit ctx: Context) = Try(Drawable.createFromStream(ctx.getAssets.open(path), null))
    .getOrElse(ctx.getResources.getDrawable(R.drawable.ic_load_unknown))

  def toBitmap(implicit ctx: Context): Bitmap = Try(BitmapFactory.decodeStream(ctx.getAssets.open(path)))
    .getOrElse(R.drawable.ic_load_unknown.toBitmap)
}

sealed trait FontFile extends AssetFile {
  def toTypeFace(implicit ctx: Context): Typeface = Typeface.createFromAsset(ctx.getAssets, path)
}

case class ChampIconAsset(champName: String) extends ImageFile {
  override protected def path: String = "champ-icons/" + champName.toLowerCase.replaceAll("[^a-zA-Z]", "") + ".png"
}

case class SummonerSpellAsset(spellName: String) extends ImageFile {
  override protected def path: String = "summoner-spells/" + spellName.toLowerCase + ".png"
}

case class FrizFontAsset() extends FontFile {
  override protected def path: String = "fonts/friz-quadrata.ttf"
}