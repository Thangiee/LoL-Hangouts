package com.thangiee.LoLHangouts.api

import scala.util.Random

object Keys {
  val keys = List(

  )

  val masterKey = ""

  def randomKey: String = Random.shuffle(keys).head
}
