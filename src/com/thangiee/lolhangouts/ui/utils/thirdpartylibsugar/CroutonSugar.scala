package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.app.Activity
import android.content.Context
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.utils._
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

trait CroutonSugar {

  implicit class CroutonSugar(string: String)(implicit ctx: Context) {
    def makeCrouton(style: Style, duration: Int): Crouton = Crouton
        .makeText(ctx.asInstanceOf[Activity], string, style, R.id.crouton_holder)
        .setConfiguration(new Configuration.Builder().setDuration(duration).build())

    def croutonWarn(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.ALERT, duration).show())
    def croutonConfirm(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.CONFIRM, duration).show())
    def croutonInfo(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.INFO, duration).show())
  }
}

object CroutonSugar extends CroutonSugar
