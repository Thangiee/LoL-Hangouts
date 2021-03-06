package com.thangiee.lolhangouts.ui.core

import android.support.v7.app.AppCompatActivity
import android.view._
import org.scaloid.common.TagUtil

trait Container extends AnyRef with TagUtil {
  self: ViewGroup =>

  // assuming the parent activity is an instance of TActivity
  val toolbar = getContext.asInstanceOf[TActivity].toolbar
  val navIcon = getContext.asInstanceOf[TActivity].navIcon

  override def onWindowVisibilityChanged(visibility: Int): Unit = {
    self.onWindowVisibilityChanged(visibility)
    visibility match {
      case View.VISIBLE   => onVisible()
      case View.INVISIBLE =>
      case View.GONE      => onInvisible()
    }
  }

  def onInvisible(): Unit = {}

  def onVisible(): Unit = {}
  
  def getView: View

  def onPrepareOptionsMenu(menu: Menu): Boolean = false

  def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = false

  def onOptionsItemSelected(item: MenuItem): Boolean = false

  def invalidateOptionsMenu(): Unit = getContext.asInstanceOf[AppCompatActivity].invalidateOptionsMenu()

  def onBackPressed(): Boolean = false

  def onNavIconClick(): Boolean = false
}
