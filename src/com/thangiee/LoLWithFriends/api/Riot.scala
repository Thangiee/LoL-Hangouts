package com.thangiee.LoLWithFriends.api

import jriot.main.JRiot

object Riot {
  val api = new JRiot()
  api.setApiKey("YOUR-API-KEY")
  api.setRegion("na")
}
