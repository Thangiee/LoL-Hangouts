package com.thangiee.LoLHangouts.activities

import android.content.DialogInterface
import com.thangiee.LoLHangouts.utils.TLogger
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

trait TActivity extends org.scaloid.common.SActivity with TLogger {
  override def onDestroy(): Unit = {
    super.onDestroy()
  }

  implicit def function2DialogOnClickListener(f: DialogInterface => Unit) : DialogInterface.OnClickListener = {
    new DialogInterface.OnClickListener() {
      override def onClick(dialog: DialogInterface, which: Int): Unit = f(dialog)
    }
  }

  implicit class ResTo(id: Int) {
    def r2Color: Int = getResources.getColor(id)
  }

  implicit class StringTo(string: String) {
    def makeCrouton(style: Style = Style.ALERT, duration: Int = Configuration.DURATION_SHORT) {
      Crouton.makeText(ctx, string, style).setConfiguration(new Configuration.Builder().setDuration(duration).build()).show()
    }
  }
}
