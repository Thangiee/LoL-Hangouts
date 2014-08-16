package com.thangiee.LoLHangouts.fragments

import android.app.Fragment
import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.utils.ConversionImplicits
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import org.scaloid.common.{SystemService, InterfaceImplicits, TagUtil}

trait TFragment extends Fragment with InterfaceImplicits with ConversionImplicits with SystemService with TagUtil {
  implicit lazy val ctx: Context = getActivity
  var view: View = _

  def find[V <: View](id: Int): V = view.findViewById(id).asInstanceOf[V]

  def info(s: String) = org.scaloid.common.info(s)

  def warn(s: String) = org.scaloid.common.warn(s)

  def debug(s: String) = org.scaloid.common.debug(s)

  def wtf(s: String) = org.scaloid.common.wtf(s)

  def verbose(s: String) = org.scaloid.common.verbose(s)

  def runOnUiThread(code: => Unit) = new {
    getActivity.runOnUiThread(new Runnable {
      override def run(): Unit = code
    })
  }

  override def onDestroy(): Unit = {
    Crouton.cancelAllCroutons()
    super.onDestroy()
  }

  implicit class StringTo(string: String) {
    def makeCrouton(style: Style = Style.ALERT, duration: Int = Configuration.DURATION_SHORT) {
      Crouton.makeText(getActivity, string, style).setConfiguration(new Configuration.Builder().setDuration(duration).build()).show()
    }
  }
}
