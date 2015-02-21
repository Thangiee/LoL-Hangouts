package com.thangiee.lolhangouts.data.usecases.entities


case class AppData
(saveUsername: String,
 savePassword: String,
 version: String,
 isLoginOffline: Boolean,
 selectedRegion: Option[Region]
  )
