package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.{ButtonCallback, InputCallback, ListCallbackMultiChoice, ListCallbackSingleChoice}

import scala.language.implicitConversions

trait MaterialDialogSugar {
  type MultiChoice = (MaterialDialog, Array[Int], Array[CharSequence]) => Boolean
  type SingleChoice = (MaterialDialog, View, Int, CharSequence) => Boolean

  implicit class MaterialDialogSugar(builder: MaterialDialog.Builder) {
    private var positiveListener: Option[MaterialDialog => Unit] = None
    private var negativeListener: Option[MaterialDialog => Unit] = None
    private var neutralListener: Option[MaterialDialog => Unit] = None
    private var multiChoice: Option[MultiChoice] = None
    private var singleChoice: Option[SingleChoice] = None

    builder.callback(new ButtonCallback {
      override def onPositive(dialog: MaterialDialog): Unit = positiveListener.foreach(l => l(dialog))
      override def onNegative(dialog: MaterialDialog): Unit = negativeListener.foreach(l => l(dialog))
      override def onNeutral(dialog: MaterialDialog): Unit = negativeListener.foreach(l => l(dialog))
    })

    def onSingleChoice(f: SingleChoice): MaterialDialog.Builder = {
      singleChoice = Some(f)
      builder.itemsCallbackSingleChoice(0, f)
    }

    def onMultiChoice(f: MultiChoice): MaterialDialog.Builder = {
      multiChoice = Some(f)
      builder.itemsCallbackMultiChoice(null, f)
    }

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

  implicit def func2InputCallback(f: (MaterialDialog, CharSequence) => Unit): InputCallback = new InputCallback {
    def onInput(dialog: MaterialDialog, input: CharSequence): Unit = f(dialog, input)
  }

  implicit def func2ListCallbackMultiChoice(f: MultiChoice): ListCallbackMultiChoice =
    new ListCallbackMultiChoice {
      def onSelection(dialog: MaterialDialog, which: Array[Integer], text: Array[CharSequence]): Boolean = f(dialog, which.map(_.toInt), text)
    }

  implicit def func2ListCallbackSingleChoice(f: SingleChoice): ListCallbackSingleChoice =
    new ListCallbackSingleChoice {
      def onSelection(dialog: MaterialDialog, view: View, i: Int, selection: CharSequence): Boolean = f(dialog, view, i, selection)
    }
}

object MaterialDialogSugar extends MaterialDialogSugar
