package com.thangiee.lolhangouts.data.datasources.entities

case class AppDataEntity(
  saveUsername: String,
  savePassword: String,
  version: String,
  isLoginOffline: Boolean,
  selectedRegionId: Option[String],
  isGuestMode: Boolean
  )
