package com.thangiee.lolhangouts.data.datasources.entities.mappers

import com.thangiee.lolhangouts.data.datasources.entities.AppDataEntity
import com.thangiee.lolhangouts.data.usecases.entities.{Region, AppData}

object AppDataMapper {

  def transform(appDataEntity: AppDataEntity): AppData = {
    AppData(
      appDataEntity.saveUsername,
      appDataEntity.savePassword,
      appDataEntity.version,
      appDataEntity.isLoginOffline,
      appDataEntity.selectedRegionId.map(id => Region.getFromId(id))
    )
  }
}
