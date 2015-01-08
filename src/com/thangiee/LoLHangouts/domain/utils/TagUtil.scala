package com.thangiee.LoLHangouts.domain.utils

case class LoggerTag(tag: String)

trait TagUtil {
  implicit val loggerTag = LoggerTag(this.getClass.getSimpleName)
}
