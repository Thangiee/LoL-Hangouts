package com.thangiee.lolhangouts.data.entities.mappers

import com.thangiee.lolhangouts.data.entities.AppDataEntity
import com.thangiee.lolhangouts.domain.entities.{Region, AppData}

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
