package com.thangiee.LoLHangouts.data.entities

case class AppDataEntity
(saveUsername: String,
 savePassword: String,
 version: String,
 isLoginOffline: Boolean,
 selectedRegionId: Option[String]
  )
