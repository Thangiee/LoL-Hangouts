package com.thangiee.LoLHangouts

import android.view.{View, ViewGroup}
import com.thangiee.LoLHangouts.domain.utils.TagUtil
import org.scaloid.common.TraitViewGroup

trait CustomView extends TraitViewGroup[ViewGroup] with TagUtil {
  self: ViewGroup =>
  override def basis: ViewGroup = self

  def presenter: Presenter

  override def onAttachedToWindow(): Unit = {
    self.onAttachedToWindow()
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

  def onVisible(): Unit = {}

  def onInvisible(): Unit = {}

  override def onDetachedFromWindow(): Unit = {
    presenter.shutdown()
    self.onDetachedFromWindow()
  }
}
