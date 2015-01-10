package com.thangiee.LoLHangouts

import android.support.v7.app.ActionBarActivity
import android.view.{MenuInflater, Menu, View, ViewGroup}
import com.thangiee.LoLHangouts.activities.TActivity
import org.scaloid.common.TagUtil

trait Container extends AnyRef with TagUtil {
  self: ViewGroup =>

  // assuming the parent activity is an instance of TActivity
  val toolbar = getContext.asInstanceOf[TActivity].toolbar
  val materialMenu = getContext.asInstanceOf[TActivity].navIcon

  def getView: View

  def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean

  def invalidateOptionsMenu(): Unit = getContext.asInstanceOf[ActionBarActivity].invalidateOptionsMenu()

  def onBackPressed(): Boolean

  def onNavIconClick(): Boolean
}
