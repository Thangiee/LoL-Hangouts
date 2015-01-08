package com.thangiee.LoLHangouts

import android.view.{View, ViewGroup}
import com.thangiee.LoLHangouts.domain.utils.TagUtil
import org.scaloid.common.TraitViewGroup
import com.thangiee.LoLHangouts.domain.utils.Logger._

trait CustomView extends TraitViewGroup[ViewGroup] with TagUtil {
  self: ViewGroup =>
  override def basis: ViewGroup = self

  def presenter: Presenter

  override def onAttachedToWindow(): Unit = {
    self.onAttachedToWindow()
    verbose("[*] onAttached")
    presenter.initialize()
  }

  override def onWindowVisibilityChanged(visibility: Int): Unit = {
    self.onWindowVisibilityChanged(visibility)
    visibility match {
      case View.VISIBLE   => verbose("[*] onVisible"); onVisible(); presenter.resume()
      case View.INVISIBLE =>
      case View.GONE      => verbose("[*] onInvisible"); onInvisible(); presenter.pause()
    }
  }

  def onVisible(): Unit = {}

  def onInvisible(): Unit = {}

  override def onDetachedFromWindow(): Unit = {
    verbose("[*] onDetached")
    presenter.shutdown()
    self.onDetachedFromWindow()
  }
}
