package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import fr.castorflex.android.circularprogressbar.{CircularProgressBar, CircularProgressDrawable}

trait CircularProgressBarSugar {

  implicit class CircularProgressBarHelper(v: CircularProgressBar) {
    private lazy val drawable = v.getIndeterminateDrawable.asInstanceOf[CircularProgressDrawable]
    def stop() = drawable.stop()
    def start() = drawable.start()
    def restart() = { drawable.stop(); drawable.start() }
  }
}

object CircularProgressBarSugar extends CircularProgressBarSugar
