package com.thangiee.LoLHangouts.data.entities.mappers

import com.thangiee.LoLHangouts.data.entities.AppDataEntity
import com.thangiee.LoLHangouts.domain.entities.{Region, AppData}

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
