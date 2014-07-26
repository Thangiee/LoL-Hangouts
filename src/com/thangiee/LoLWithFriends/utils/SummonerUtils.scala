package com.thangiee.LoLWithFriends.utils

import com.thangiee.LoLWithFriends.api.Server

object SummonerUtils {

  def profileIconUrl(name: String, server: Server): String = {
    "http://avatar.leagueoflegends.com/"+server+"/"+name+".png"
  }
}
