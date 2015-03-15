package com.thangiee.lolhangouts.ui.sidedrawer

case class DrawerItem(title: String, icon: Int, var isSelected: Boolean = false)

object DrawerItem {
  val Chat        = "Chat"
  val Profile     = "My Profile"
  val Search      = "Search Summoner"
  val GameScouter = "Game Scouter"
  val Settings    = "Settings"
  val RemoveAds   = "Remove Ads"
  val Logout      = "Logout"
}