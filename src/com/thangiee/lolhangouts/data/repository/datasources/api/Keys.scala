package com.thangiee.lolhangouts.data.repository.datasources.api

import android.util.Base64

import scala.util.Random

object Keys {
  val testKeys = List(
    ""
  )

  private val productionKey_ = ""

  def testKey: String = new String(Base64.decode(Random.shuffle(testKeys).head, 0))

  def productionKey: String = new String(Base64.decode(productionKey_, 0))
}