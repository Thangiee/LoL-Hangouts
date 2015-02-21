package com.thangiee.lolhangouts.ui.core

import com.thangiee.lolhangouts.ui.utils.Logger._
import org.scaloid.common.TagUtil

trait Presenter extends AnyRef with TagUtil {

  def initialize(): Unit = {
    verbose("[*] initialize")
  }

  def resume(): Unit = {
    verbose("[*] resume")
  }

  def pause(): Unit = {
    verbose("[*] pause")
  }

  def shutdown(): Unit = {
    verbose("[*] shutdown")
  }

}
