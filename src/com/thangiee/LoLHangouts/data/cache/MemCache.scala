package com.thangiee.LoLHangouts.data.cache

import java.util.concurrent.TimeUnit

import com.google.common.cache.{Cache, CacheBuilder}
import com.thangiee.LoLHangouts.domain.utils.TagUtil
import com.thangiee.LoLHangouts.domain.utils._

object MemCache extends AnyRef with TagUtil {
  private val cache: Cache[String, Object] = CacheBuilder.newBuilder()
    .concurrencyLevel(4)
    .maximumSize(10000)
    .expireAfterWrite(20, TimeUnit.MINUTES)
    .build[String, Object]()

  def get[T](key: String): Option[T] = {
    val result = cache.getIfPresent(key)

    if (result != null) {
      debug(s"[+] mem cache hit: [$key, $result]")
      Some(result.asInstanceOf[T])
    } else {
      debug(s"[-] mem cache miss: $key")
      None
    }
  }

  def put(key: String, value: Object): Unit = {
    debug(s"[*] saving to mem cache [$key, $value]")
    cache.put(key, value)
  }

  def removeAll(): Unit = {
    cache.invalidateAll()
    cache.cleanUp()
  }
}
