package com.thangiee.LoLWithFriends.activities

import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

trait TActivity extends org.scaloid.common.SActivity {
  override def onDestroy(): Unit = {
    super.onDestroy()
  }

  implicit class StringTo(string: String) {
    def makeCrouton(style: Style = Style.ALERT, duration: Int = Configuration.DURATION_SHORT) {
      Crouton.makeText(ctx, string, style).setConfiguration(new Configuration.Builder().setDuration(duration).build()).show()
    }
  }
}
