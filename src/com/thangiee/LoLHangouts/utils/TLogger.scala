package com.thangiee.LoLHangouts.utils

import org.scaloid.common.TagUtil

trait TLogger extends TagUtil {
  val LOGLEVEL = 0
  val DEBUG = LOGLEVEL == 0

  def verbose(s: String) = if (DEBUG) org.scaloid.common.verbose(s)

  def debug(s: String) = if (DEBUG) org.scaloid.common.debug(s)

  def info(s: String) = if (DEBUG) org.scaloid.common.info(s)

  def warn(s: String) = org.scaloid.common.warn(s)

  def wtf(s: String) = org.scaloid.common.wtf(s)
}
