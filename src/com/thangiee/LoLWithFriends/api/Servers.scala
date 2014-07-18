package com.thangiee.LoLWithFriends.api

trait Servers {
  val url: String
}

object BR extends Servers {
  override val url: String = "chat.br.lol.riotgames.com"
}

object EUNE extends Servers {
  override val url: String = "chat.eun1.riotgames.com"
}

object EUW extends Servers {
  override val url: String = "chat.euw1.lol.riotgames.com"
}

object KR extends Servers {
  override val url: String = "chat.kr.lol.riotgames.com"
}

object LAN extends Servers {
  override val url: String = "chat.la1.lol.riotgames.com"
}

object LAS extends Servers {
  override val url: String = "chat.la2.lol.riotgames.com"
}

object NA extends Servers {
  override val url: String = "chat.na1.lol.riotgames.com"
}

object OCE extends Servers {
  override val url: String = "chat.oc1.lol.riotgames.com"
}

object PBE extends Servers {
  override val url: String = "chat.pbe1.lol.riotgames.com"
}

object PH extends Servers {
  override val url: String = "chatph.lol.garenanow.com"
}

object RU extends Servers {
  override val url: String = "chat.ru.lol.riotgames.com"
}

object TH extends Servers {
  override val url: String = "chatth.lol.garenanow.com"
}

object TR extends Servers {
  override val url: String = "chat.tr.lol.riotgames.com"
}

object TW extends Servers {
  override val url: String = "chattw.lol.garenanow.com"
}

object VN extends Servers {
  override val url: String = "chatvn.lol.garenanow.com"
}
