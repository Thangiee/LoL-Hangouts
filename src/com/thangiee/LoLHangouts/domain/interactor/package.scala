package com.thangiee.LoLHangouts.domain

import com.thangiee.LoLHangouts.domain.utils._

package object interactor {

  implicit class LogAndThrow[A](either: Either[Exception, A]) {
    def ifErrorThenLogAndThrow()(implicit loggerTag: LoggerTag): A = {
      either.fold(
        err => { error(s"[!] ${err.getMessage}", err.getCause); throw err },
        data => data
      )
    }
  }

  implicit class LogAndReturn[A](a: A) {
    def orElseLogAndReturn(logMessage: String)(implicit loggerTag: LoggerTag): A = {
      info("[+] " + logMessage)
      a
    }
  }
}
