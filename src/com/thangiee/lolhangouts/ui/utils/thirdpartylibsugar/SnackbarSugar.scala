package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import com.nispok.snackbar.Snackbar
import com.nispok.snackbar.listeners.ActionClickListener

trait SnackbarSugar {

  implicit def func2ActionClickListener(f: Snackbar => Unit): ActionClickListener = {
    new ActionClickListener {
      override def onActionClicked(snackbar: Snackbar): Unit = f(snackbar)
    }
  }
}

object SnackbarSugar extends SnackbarSugar
