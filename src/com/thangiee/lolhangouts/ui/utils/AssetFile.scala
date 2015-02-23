package com.thangiee.lolhangouts.ui.utils

sealed trait AssetFile {
  def path: String
}

case class ChampIconAssetFile(champName: String) extends AssetFile {
  override def path: String = "champ-icons/" + champName.toLowerCase.replaceAll("[^a-zA-Z]", "") + ".png"
}

case class SummonerSpellAssetFile(spellName: String) extends AssetFile {
  override def path: String = "summoner-spells/" + spellName.toLowerCase + ".png"
}