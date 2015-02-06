package com.thangiee.lolhangouts.domain.entities


case class AppData
(saveUsername: String,
 savePassword: String,
 version: String,
 isLoginOffline: Boolean,
 selectedRegion: Option[Region]
  )
