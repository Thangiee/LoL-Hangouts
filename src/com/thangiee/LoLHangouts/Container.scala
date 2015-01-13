package com.thangiee.LoLHangouts

import android.support.v7.app.ActionBarActivity
import android.view._
import com.thangiee.LoLHangouts.activities.TActivity
import org.scaloid.common.TagUtil

trait Container extends AnyRef with TagUtil {
  self: ViewGroup =>

  // assuming the parent activity is an instance of TActivity
  val toolbar = getContext.asInstanceOf[TActivity].toolbar
  val navIcon = getContext.asInstanceOf[TActivity].navIcon

  def getView: View

  def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = false

  def onOptionsItemSelected(item: MenuItem): Boolean = false

  def invalidateOptionsMenu(): Unit = getContext.asInstanceOf[ActionBarActivity].invalidateOptionsMenu()

  def onBackPressed(): Boolean = false

  def onNavIconClick(): Boolean = false
}
