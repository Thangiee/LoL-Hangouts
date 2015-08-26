package com.thangiee.lolhangouts.data.datasources.cache

import scala.concurrent.duration.Duration

trait CanCache[T] {

  def get(key: String): Option[T]

  def put(keyVal: (String, T), ttl: Option[Duration] = None): Unit

}
