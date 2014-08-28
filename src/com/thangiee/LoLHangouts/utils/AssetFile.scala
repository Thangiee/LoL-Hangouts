package com.thangiee.LoLHangouts.utils

sealed trait AssetFile {
  def path: String
}

case class ChampIconAssetFile(champName: String) extends AssetFile {
  override def path: String = "champ-icons/" + champName.toLowerCase.replaceAll("[^a-zA-Z]", "") + ".png"
}
