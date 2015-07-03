package com.thangiee.lolhangouts.data

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

package object utils extends AnyRef with Logger {
  object Implicits extends AnyRef {
    implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  }
}
