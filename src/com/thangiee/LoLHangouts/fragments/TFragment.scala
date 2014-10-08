package com.thangiee.LoLHangouts.fragments

import android.app.Fragment
import android.content.Context
import android.os.{Bundle, Handler, Looper}
import android.view.View
import com.thangiee.LoLHangouts.utils.{TContext, TLogger}
import de.keyboardsurfer.android.widget.crouton.Crouton
import org.scaloid.common.{Implicits, SystemService}

trait TFragment extends Fragment with TContext with Implicits with SystemService with TLogger {
  override implicit lazy val ctx: Context = getActivity
  private lazy val uiThread = Looper.getMainLooper.getThread
  private lazy val handler  = new Handler(Looper.getMainLooper)
  var view: View = _

  def find[V <: View](id: Int): V = view.findViewById(id).asInstanceOf[V]

  def args(arguments: (String, Any)*): this.type = {
    val bundle = new Bundle()

    for ((k, v) ← arguments) {
      v match {
        case v: String => bundle.putString(k, v)
        case v: Int => bundle.putInt(k, v)
        case v: Double => bundle.putDouble(k, v)
        case v: Float => bundle.putFloat(k, v)
        case v: Boolean => bundle.putBoolean(k, v)
        case v: Serializable => bundle.putSerializable(k, v)
      }
    }
    this.setArguments(bundle)
    this
  }

  def runOnUiThread[T >: Null](f: => T): T = {
    if (uiThread == Thread.currentThread) {
      f
    } else {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })
      null
    }
  }

  override def onDestroy(): Unit = {
    Crouton.cancelAllCroutons()
    super.onDestroy()
  }
}
