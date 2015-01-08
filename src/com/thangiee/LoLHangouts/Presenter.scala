package com.thangiee.LoLHangouts

import com.thangiee.LoLHangouts.domain.utils.Logger._
import com.thangiee.LoLHangouts.domain.utils.TagUtil

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
