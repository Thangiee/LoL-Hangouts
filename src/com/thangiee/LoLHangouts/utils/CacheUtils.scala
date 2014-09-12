package com.thangiee.LoLHangouts.utils

import java.util.concurrent.TimeUnit

import com.google.common.cache.{Cache, CacheBuilder}

object CacheUtils {
  private val cache: Cache[String, Object] = CacheBuilder.newBuilder()
    .concurrencyLevel(4)
    .maximumSize(10000)
    .expireAfterWrite(20, TimeUnit.MINUTES)
    .build[String, Object]()

  def get[T](key: String): T = {
    cache.getIfPresent(key).asInstanceOf[T]
  }

  def put(key: String, value: Object): Unit = {
    cache.put(key, value)
  }

  def cleanUp(): Unit = {
    cache.invalidateAll()
    cache.cleanUp()
  }
}
