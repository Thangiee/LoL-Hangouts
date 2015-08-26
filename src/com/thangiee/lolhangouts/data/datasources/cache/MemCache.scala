package com.thangiee.lolhangouts.data.datasources.cache

import scala.concurrent.duration.Duration
import scalacache.guava.GuavaCache

object MemCache {
  import scalacache._
  implicit private val scalaCache = ScalaCache(GuavaCache())

  implicit object StringMemCache extends CanCache[String] {
    def get(key: String): Option[String] = getSync[String](key)
    def put(keyVal: (String, String), ttl: Option[Duration]): Unit = keyVal match {
      case (k, v) => scalacache.put(k)(v, ttl)
    }
  }

  implicit object IntMemCache extends CanCache[Int] {
    def get(key: String): Option[Int] = getSync[Int](key)
    def put(keyVal: (String, Int), ttl: Option[Duration]): Unit = keyVal match {
      case (k, v) => scalacache.put(k)(v, ttl)
    }
  }

}
