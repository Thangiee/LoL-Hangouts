package com.thangiee.LoLHangouts.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.View
import com.thangiee.LoLHangouts.utils.Logger
import de.keyboardsurfer.android.widget.crouton.Crouton

trait TFragment extends Fragment with Logger {
  implicit lazy val ctx: Context = getActivity
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

  override def onDestroy(): Unit = {
    Crouton.cancelAllCroutons()
    super.onDestroy()
  }
}
