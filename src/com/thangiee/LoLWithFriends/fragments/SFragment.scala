package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.content.Context
import android.view.View
import com.thangiee.LoLWithFriends.utils.ConversionImplicits
import org.scaloid.common.{InterfaceImplicits, TagUtil}

trait SFragment extends Fragment with InterfaceImplicits with ConversionImplicits with TagUtil {
  implicit lazy val context: Context = getActivity
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
}
