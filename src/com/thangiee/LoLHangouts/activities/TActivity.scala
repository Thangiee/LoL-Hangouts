package com.thangiee.LoLHangouts.activities

import com.thangiee.LoLHangouts.utils.{TContext, TLogger}

trait TActivity extends org.scaloid.common.SActivity with TContext with TLogger {
  override def onDestroy(): Unit = {
    super.onDestroy()
  }
}
