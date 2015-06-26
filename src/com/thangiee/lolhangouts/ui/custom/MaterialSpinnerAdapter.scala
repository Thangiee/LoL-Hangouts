package com.thangiee.lolhangouts.ui.custom

import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseAdapter, TextView}
import com.thangiee.lolhangouts.R

class MaterialSpinnerAdapter(items: Seq[String] = Nil) extends BaseAdapter {
  private var _items = items

  def clear(): Unit = _items = Nil

  def addItem(item: String): Unit = _items ++= Seq(item)

  def addItems(items: Seq[String]): Unit = _items ++= items

  def getItemId(i: Int): Long = i

  def getCount: Int = _items.size

  def getItem(i: Int): String = _items(i)

  override def getDropDownView(i: Int, v: View, viewGroup: ViewGroup): View = {
    var view = v
    if (view == null || !(view.getTag.toString == "DROPDOWN")) {
      view = LayoutInflater.from(viewGroup.getContext).inflate(R.layout.toolbar_spinner_item_dropdown, viewGroup, false)
      view.setTag("DROPDOWN")
    }
    val textView = view.findViewById(android.R.id.text1).asInstanceOf[TextView]
    textView.setText(getTitle(i))
    view
  }

  def getView(i: Int, v: View, viewGroup: ViewGroup): View = {
    var view = v
    if (view == null || !(view.getTag.toString == "NON_DROPDOWN")) {
      view = LayoutInflater.from(viewGroup.getContext).inflate(R.layout.toolbar_spinner_item_actionbar, viewGroup, false)
      view.setTag("NON_DROPDOWN")
    }
    val textView = view.findViewById(android.R.id.text1).asInstanceOf[TextView]
    textView.setText(getTitle(i))
    view
  }

  private def getTitle(position: Int): String =
    if (position >= 0 && position < _items.size) _items(position) else "???"
}
