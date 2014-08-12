package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.content.Context
import android.view.View
import com.thangiee.LoLWithFriends.utils.ConversionImplicits
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import org.scaloid.common.{InterfaceImplicits, TagUtil}

trait SFragment extends Fragment with InterfaceImplicits with ConversionImplicits with TagUtil {
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
    info("[*] Canceling any remaining croutons.")
    Crouton.cancelAllCroutons()
    super.onDestroy()
  }

  implicit class StringTo(string: String) {
    def makeCrouton(style: Style, duration: Int = Configuration.DURATION_SHORT) {
      Crouton.makeText(getActivity, string, style).setConfiguration(new Configuration.Builder().setDuration(duration).build()).show()
    }
  }
}
