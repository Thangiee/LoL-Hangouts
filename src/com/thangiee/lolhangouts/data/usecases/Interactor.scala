package com.thangiee.lolhangouts.data.usecases

import com.thangiee.lolhangouts.data.datasources.api.RiotApiCaller
import com.thangiee.lolhangouts.data.utils._

trait Interactor extends AnyRef with TagUtil with RiotApiCaller {

  object CacheIn {
    val Memory = com.thangiee.lolhangouts.data.datasources.cache.MemCache
  }

  private [data] val Good = org.scalactic.Good
  private [data] val Bad = org.scalactic.Bad

  implicit class LogAndReturn[A](a: A) {
    def logThenReturn(logMessage: (A) => String)(implicit loggerTag: LoggerTag): A = {
      info(logMessage.apply(a))
      a
    }
  }
}
