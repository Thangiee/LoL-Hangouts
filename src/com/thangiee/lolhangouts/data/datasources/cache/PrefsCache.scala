package com.thangiee.lolhangouts.data.datasources.cache

import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.lolhangouts.data._

object PrefsCache extends AnyRef with TagUtil {

  def put[T](keyVal: (String, T)): Unit = {
    keyVal match {
      case (k: String, v: String)  => Prefs.putString(k, v)
      case (k: String, v: Int)     => Prefs.putInt(k, v)
      case (k: String, v: Long)    => Prefs.putLong(k, v)
      case (k: String, v: Float)   => Prefs.putFloat(k, v)
      case (k: String, v: Boolean) => Prefs.putBoolean(k, v)
      case _                       => throw new IllegalArgumentException("Unsupported value type.")
    }
  }

  def getString(key: String): Option[String] = {
    Prefs.getString(key, null) match {
      case v: String => debug(s"[+] Prefs cache hit: [$key, $v]"); Some(v)
      case _         => debug(s"[-] Prefs cache miss: $key"); None
    }
  }

  def getInt(key: String, defValue: Int): Int = {
    val result = Prefs.getInt(key, defValue)
    if (result != defValue) debug(s"[+] Prefs cache hit: [$key, $result]") else debug(s"[-] Prefs cache miss: $key")
    result
  }

  def getLong(key: String, defValue: Long): Long = {
    val result = Prefs.getLong(key, defValue)
    if (result != defValue) debug(s"[+] Prefs cache hit: [$key, $result]") else debug(s"[-] Prefs cache miss: $key")
    result
  } 

  def getFloat(key: String, defValue: Float): Float = {
    val result = Prefs.getFloat(key, defValue)
    if (result != defValue) debug(s"[+] Prefs cache hit: [$key, $result]") else debug(s"[-] Prefs cache miss: $key")
    result
  }

  def getBoolean(key: String, defValue: Boolean): Boolean = {
    val result = Prefs.getBoolean(key, defValue)
    if (result != defValue) debug(s"[+] Prefs cache hit: [$key, $result]") else debug(s"[-] Prefs cache miss: $key")
    result
  } 

}
