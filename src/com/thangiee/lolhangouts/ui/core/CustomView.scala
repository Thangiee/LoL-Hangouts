package com.thangiee.lolhangouts.ui.core

import android.view.{View, ViewGroup}
import com.thangiee.lolhangouts.utils._
import org.scaloid.common.{TagUtil, TraitViewGroup}

trait CustomView extends TraitViewGroup[ViewGroup] with TagUtil {
  self: ViewGroup =>
  override def basis: ViewGroup = self

  def presenter: Presenter

  override def onAttachedToWindow(): Unit = {
    self.onAttachedToWindow()
    onAttached()
    presenter.initialize()
  }

  override def onWindowVisibilityChanged(visibility: Int): Unit = {
    self.onWindowVisibilityChanged(visibility)
    visibility match {
      case View.VISIBLE   => onVisible(); presenter.resume()
      case View.INVISIBLE =>
      case View.GONE      => onInvisible(); presenter.pause()
    }
  }

  def onAttached(): Unit = { verbose("[*] onAttached") }

  def onVisible(): Unit = { verbose("[*] onVisible") }

  def onInvisible(): Unit = { verbose("[*] onInvisible") }

  def onDetached(): Unit = { verbose("[*] onDetached") }

  override def onDetachedFromWindow(): Unit = {
    onDetached()
    presenter.shutdown()
    self.onDetachedFromWindow()
  }
}
