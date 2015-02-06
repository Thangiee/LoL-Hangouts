package com.thangiee.lolhangouts.utils

import com.typesafe.scalalogging.{Logger => Log}
import org.scaloid.common.LoggerTag
import org.slf4j.LoggerFactory

trait Logger {
  private def logger(implicit loggerTag: LoggerTag) = Log.apply(LoggerFactory.getLogger(loggerTag.tag))
  private def out(s: String)(implicit loggerTag: LoggerTag) = "[%-15s] %s".format(loggerTag.tag.takeRight(15), s)

  def verbose(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.trace(out(s), if (t == null) "" else t)
  def debug(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.debug(out(s), if (t == null) "" else t)
  def info(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.info(out(s), if (t == null) "" else t)
  def warn(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.warn(out(s), if (t == null) "" else t)
  def error(s: => String, t: Throwable = null)(implicit loggerTag: LoggerTag) = logger.error(out(s), if (t == null) "" else t)
}
object Logger extends Logger
