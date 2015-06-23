package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback

trait MaterialDialogSugar {

  implicit class MaterialDialogSugar(builder: MaterialDialog.Builder) {
    private var positiveListener: Option[MaterialDialog => Unit] = None
    private var negativeListener: Option[MaterialDialog => Unit] = None
    private var neutralListener: Option[MaterialDialog => Unit] = None

    builder.callback(new ButtonCallback {
      override def onPositive(dialog: MaterialDialog): Unit = positiveListener.foreach(l => l(dialog))
      override def onNegative(dialog: MaterialDialog): Unit = negativeListener.foreach(l => l(dialog))
      override def onNeutral(dialog: MaterialDialog): Unit = negativeListener.foreach(l => l(dialog))
    })

    def onPositive(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      positiveListener = Some(f)
      builder
    }

    def onNegative(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      negativeListener = Some(f)
      builder
    }

    def onNeutral(f: MaterialDialog => Unit): MaterialDialog.Builder = {
      neutralListener = Some(f)
      builder
    }
  }
}

object MaterialDialogSugar extends MaterialDialogSugar
