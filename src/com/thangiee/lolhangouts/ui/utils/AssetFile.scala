package com.thangiee.lolhangouts.ui.utils

sealed trait AssetFile {
  def path: String
}

sealed trait ImageFile extends AssetFile
sealed trait FontFile extends AssetFile

case class ChampIconAsset(champName: String) extends ImageFile {
  override def path: String = "champ-icons/" + champName.toLowerCase.replaceAll("[^a-zA-Z]", "") + ".png"
}

case class SummonerSpellAsset(spellName: String) extends ImageFile {
  override def path: String = "summoner-spells/" + spellName.toLowerCase + ".png"
}

case class FrizFontAsset() extends FontFile {
  override def path: String = "fonts/friz-quadrata.ttf"
}