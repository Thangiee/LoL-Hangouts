package com.thangiee.LoLHangouts.domain.entities

object Value {
  object String {
    type Version = String
    type Username = String
    type Password = String
  }

  object Boolean {
    type IsNewVersion = Boolean
    type IsLoginOffline = Boolean
  }
}
