package com.thangiee.LoLHangouts

import com.thangiee.LoLHangouts.utils.Logger._
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
