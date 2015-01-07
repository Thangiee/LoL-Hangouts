package com.thangiee.LoLHangouts.domain.utils

import com.typesafe.scalalogging.{Logger => Log}
import org.slf4j.LoggerFactory

trait Logger {
  private def logger(implicit loggerTag: LoggerTag) = Log.apply(LoggerFactory.getLogger(loggerTag.tag))

  def verbose(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.trace(s, if (t == null) "" else t)
  def debug(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.debug(s, if (t == null) "" else t)
  def info(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.info(s, if (t == null) "" else t)
  def warn(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.warn(s, if (t == null) "" else t)
  def error(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.error(s, if (t == null) "" else t)
}
object Logger extends Logger
