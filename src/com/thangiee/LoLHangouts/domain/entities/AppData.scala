package com.thangiee.LoLHangouts.domain.entities


case class AppData
(saveUsername: String,
 savePassword: String,
 version: String,
 isLoginOffline: Boolean,
 selectedRegion: Option[Region]
  )
