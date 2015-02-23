package com.thangiee.lolhangouts.data.utils

case class LoggerTag(tag: String)

trait TagUtil {
  implicit val loggerTag = LoggerTag(this.getClass.getSimpleName)
}
