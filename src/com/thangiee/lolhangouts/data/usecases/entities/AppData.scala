package com.thangiee.lolhangouts.data.usecases.entities

import com.thangiee.lolchat.region.Region


case class AppData(
  saveUsername: String,
  savePassword: String,
  version: String,
  isLoginOffline: Boolean,
  selectedRegion: Option[Region],
  isGuestMode: Boolean
  )
