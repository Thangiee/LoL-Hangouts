package com.thangiee.lolhangouts.ui.sidedrawer

import android.content.Context
import android.graphics.Typeface
import android.view.{View, ViewGroup}
import android.widget.{BaseAdapter, ImageView, TextView}
import at.markushi.ui.RevealColorView
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.sidedrawer.DrawerItem._
import com.thangiee.lolhangouts.ui.utils._

class DrawerItemAdapter(implicit ctx: Context) extends BaseAdapter {

  private val drawerItems = List(
    DrawerItem(Chat, R.drawable.ic_drawer_chat, isSelected = true), // default selection
    DrawerItem(Profile, R.drawable.ic_drawer_person),
    DrawerItem(Search, R.drawable.ic_drawer_search),
    DrawerItem(GameScouter, R.drawable.ic_drawer_tv),
    DrawerItem(Settings, R.drawable.ic_drawer_settings),
    DrawerItem(RemoveAds, R.drawable.ic_drawer_thumb_up),
    DrawerItem(Logout, R.drawable.ic_drawer_exit))

  private var currentDrawerItem = drawerItems(0)

  override def getCount: Int = drawerItems.size

  override def getItemId(i: Int): Long = i

  override def getView(i: Int, convertView: View, viewGroup: ViewGroup): View = {
    val item = drawerItems(i)
    val view = layoutInflater.inflate(R.layout.side_menu_item, viewGroup, false)

    // drawer drawer item title and color
    view.find[TextView](R.id.tv_menu_item_name)
      .text(item.title)
      .textColor(if (item.isSelected) R.color.primary_dark.r2Color else R.color.primary_text.r2Color)
      .typeface(if (item.isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)

    // drawer drawer item icon
    view.find[ImageView](R.id.img_drawer_item)
      .imageResource(item.icon)
      .colorFilter(if (item.isSelected) R.color.md_teal_600.r2Color else R.color.md_grey_500.r2Color)

    // drawer drawer item background color
    view.find[ImageView](R.id.bg_side_menu_item)
      .backgroundColor(if (item.isSelected) R.color.md_grey_200.r2Color else R.color.md_grey_50.r2Color)

    val revealColorView = view.find[RevealColorView](R.id.reveal)
    val gestureDetector = GestureDetectorBuilder()
      .onLongPress((e) => revealColorView.ripple(e.getX, e.getY, duration = 1000))
      .onSingleTapUp((e) => revealColorView.ripple(e.getX, e.getY))
      .build

    view.onTouch((v, event) => gestureDetector.onTouchEvent(event))
    view
  }

  def setCurrentDrawer(position: Int): Unit = {
    currentDrawerItem.isSelected = false
    currentDrawerItem = drawerItems(position)
    currentDrawerItem.isSelected = true
  }

  def isDrawerSelected(position: Int): Boolean = {
    drawerItems(position).isSelected
  }

  override def getItem(i: Int): DrawerItem = drawerItems(i)
}
