package com.thangiee.LoLHangouts.fragments

import android.app.Fragment
import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.utils.{TLogger, ConversionImplicits}
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import org.scaloid.common.{InterfaceImplicits, SystemService}

trait TFragment extends Fragment with InterfaceImplicits with ConversionImplicits with SystemService with TLogger {
  implicit lazy val ctx: Context = getActivity
  var view: View = _

  def find[V <: View](id: Int): V = view.findViewById(id).asInstanceOf[V]

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
