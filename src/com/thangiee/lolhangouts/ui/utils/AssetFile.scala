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
  
  protected def defaultDrawableId: Int
  
  def toDrawable(implicit ctx: Context) = Try(Drawable.createFromStream(ctx.getAssets.open(path), null))
    .getOrElse(ctx.getResources.getDrawable(defaultDrawableId))

  def toBitmap(implicit ctx: Context): Bitmap = Try(BitmapFactory.decodeStream(ctx.getAssets.open(path)))
    .getOrElse(defaultDrawableId.toBitmap)
}

sealed trait FontFile extends AssetFile {
  def toTypeFace(implicit ctx: Context): Typeface = Typeface.createFromAsset(ctx.getAssets, path)
}

case class ChampIconAsset(champName: String) extends ImageFile {
  override protected def path: String = "champ-icons/" + champName.toLowerCase.replaceAll("[^a-zA-Z]", "") + ".png"
  override protected val defaultDrawableId: Int = R.drawable.ic_load_unknown
}

case class ItemIconAsset(itemId: Int) extends ImageFile {
  override protected def path: String = s"item-icons/item_$itemId.png"
  override protected val defaultDrawableId: Int = R.drawable.item_unknown
}

case class SummonerSpellAsset(spellName: String) extends ImageFile {
  override protected def path: String = "summoner-spells/" + spellName.toLowerCase + ".png"
  override protected val defaultDrawableId: Int = R.drawable.ic_load_unknown
}

case class FrizFontAsset() extends FontFile {
  override protected def path: String = "fonts/friz-quadrata.ttf"
}