package com.thangiee.LoLHangouts.ui.sidedrawer

case class DrawerItem(title: String, icon: Int, var isSelected: Boolean = false)

object DrawerItem {
  val Chat      = "Chat"
  val Profile   = "My Profile"
  val Search    = "Search Summoner"
  val LiveGame  = "Live Game Stats"
  val Settings  = "Settings"
  val RemoveAds = "Remove Ads"
  val Logout    = "Logout"
}