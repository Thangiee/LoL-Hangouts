package com.thangiee.lolhangouts.domain.entities

object Value {
  object String {
    type Version = String
    type Username = String
    type Password = String
    type ErrorMsg = String
  }

  object Boolean {
    type IsNewVersion = Boolean
    type IsLoginOffline = Boolean
  }
}
