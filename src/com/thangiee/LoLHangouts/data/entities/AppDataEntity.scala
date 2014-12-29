package com.thangiee.LoLHangouts.data.entities

case class AppDataEntity
(saveUsername: String,
 savePassword: String,
 saveVersion: String,
 currentVersion: String,
 isLoginOffline: Boolean,
 selectedRegionId: Option[String]
  )
