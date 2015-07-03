package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.AppDataEntity
import com.thangiee.lolhangouts.data.usecases.entities.AppData
import com.thangiee.lolhangouts.data.datasources._

object AppDataMapper {

  def transform(appDataEntity: AppDataEntity): AppData = {
    AppData(
      appDataEntity.saveUsername,
      appDataEntity.savePassword,
      appDataEntity.version,
      appDataEntity.isLoginOffline,
      appDataEntity.selectedRegionId.map(getFromId),
      appDataEntity.isGuestMode
    )
  }
}
