package com.thangiee.lolhangouts.domain

import com.thangiee.lolhangouts.domain.utils._

package object interactor {

  implicit class LogAndReturn[A](a: A) {
    def logThenReturn(logMessage: (A) => String)(implicit loggerTag: LoggerTag): A = {
      info(logMessage.apply(a))
      a
    }
  }

  implicit class ExceptionHelper(e: Exception) {
    def logThenThrow(implicit loggerTag: LoggerTag) = LogThenThrow(e)
  }

  case class LogThenThrow(ex: Exception)(implicit loggerTag: LoggerTag) {
    def v = { verbose(ex.getMessage, ex); throw ex }
    def i = { info(ex.getMessage, ex); throw ex }
    def w = { warn(ex.getMessage, ex); throw ex }
    def e = { error(ex.getMessage, ex); throw ex }
  }
}
