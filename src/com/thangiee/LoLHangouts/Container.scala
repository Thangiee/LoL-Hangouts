package com.thangiee.LoLHangouts

import android.view.{View, ViewGroup}
import com.thangiee.LoLHangouts.activities.TActivity
import org.scaloid.common.TagUtil

trait Container extends AnyRef with TagUtil {
  self: ViewGroup =>

  // assuming the parent activity is an instance of TActivity
  val toolbar = getContext.asInstanceOf[TActivity].toolbar
  val materialMenu = getContext.asInstanceOf[TActivity].navIcon

  def getView: View

  def onBackPressed(): Boolean

  def onNavIconClick(): Boolean
}
