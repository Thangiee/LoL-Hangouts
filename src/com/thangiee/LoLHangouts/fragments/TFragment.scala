package com.thangiee.LoLHangouts.fragments

import android.app.Fragment
import android.content.Context
import android.view.View
import com.thangiee.LoLHangouts.utils.{TContext, TLogger}
import de.keyboardsurfer.android.widget.crouton.Crouton
import org.scaloid.common.{Implicits, SystemService}

trait TFragment extends Fragment with TContext with Implicits with SystemService with TLogger {
  override implicit lazy val ctx: Context = getActivity
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
}
