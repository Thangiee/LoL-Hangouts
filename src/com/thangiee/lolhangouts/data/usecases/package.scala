package com.thangiee.lolhangouts.data

import com.thangiee.lolhangouts.data.utils._

package object usecases {
  private [data] val Good = org.scalactic.Good
  private [data] val Bad = org.scalactic.Bad

  implicit class LogAndReturn[A](a: A) {
    def logThenReturn(logMessage: (A) => String)(implicit loggerTag: LoggerTag): A = {
      info(logMessage.apply(a))
      a
    }
  }
}
