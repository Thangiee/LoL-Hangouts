package com.thangiee.LoLHangouts.ui.sidedrawer

import android.app.AlertDialog
import android.content.{Context, DialogInterface, Intent}
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.DrawerLayout.LayoutParams
import android.support.v7.app.ActionBarActivity
import android.util.AttributeSet
import android.view._
import android.widget._
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.activities.PreferenceSettings
import com.thangiee.LoLHangouts.domain.interactor.{ChangeUserStatusCaseImpl, GetAppDataUseCaseImpl, GetUserUseCaseImpl, LogoutUseCaseImpl}
import com.thangiee.LoLHangouts.ui.sidedrawer.DrawerItem._
import com.thangiee.LoLHangouts.ui.sidedrawer.SideDrawerView._
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.views.ConfirmDialog
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.{CustomView, R}
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu.MenuButton._
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu.{MenuButton, OnMenuButtonClick}
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay

import scala.collection.JavaConversions._

class SideDrawerView(implicit ctx: Context, a: AttributeSet) extends DrawerLayout(ctx, a) with CustomView {
  val drawerItems = List(
    DrawerItem(Chat, R.drawable.ic_action_dialog, isSelected = true), // default selection
    DrawerItem(Profile, R.drawable.ic_action_user_yellow),
    DrawerItem(Search, R.drawable.ic_action_search),
    DrawerItem(LiveGame, R.drawable.ic_action_monitor),
    DrawerItem(Settings, R.drawable.ic_action_settings),
    DrawerItem(RemoveAds, R.drawable.ic_action_like),
    DrawerItem(Logout, R.drawable.ic_action_exit))

  lazy    val drawer                         = new ListView(ctx)
  lazy    val presenceBtn = find[ExpandableMenuOverlay](R.id.btn_menu_presence)
  lazy    val statusMsgTextView = find[TextView](R.id.tv_status_msg)
  private var adapter: FunDapter[DrawerItem] = _
  private var currentDrawerItem              = drawerItems(0)

  override val presenter    = new SideDrawerPresenter(this, GetAppDataUseCaseImpl(), ChangeUserStatusCaseImpl(), GetUserUseCaseImpl(), LogoutUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    val drawerDictionary = new BindDictionary[DrawerItem]()

    // drawer drawer item title and color
    drawerDictionary.addStringField(R.id.tv_menu_item_name, (item: DrawerItem) ⇒ item.title)
      .conditionalTextColor((item: DrawerItem) ⇒ item.isSelected, R.color.my_orange.r2Color, R.color.white.r2Color)

    // drawer drawer item icon
    drawerDictionary.addStaticImageField(R.id.img_drawer_item, new StaticImageLoader[DrawerItem] {
      override def loadImage(i: DrawerItem, iv: ImageView, p: Int): Unit = iv.setImageResource(i.icon)
    })

    // drawer drawer item background color
    drawerDictionary.addStaticImageField(R.id.bg_side_menu_item, new StaticImageLoader[DrawerItem] {
      override def loadImage(i: DrawerItem, iv: ImageView, p: Int): Unit =
        iv.setBackgroundColor(if (i.isSelected) R.color.my_tran_light_blue.r2Color else android.R.color.transparent.r2Color)
    })

    adapter = new FunDapter[DrawerItem](ctx, drawerItems, R.layout.side_menu_item, drawerDictionary)

    addView(drawer)
    val width = screenAbsWidth - toolbarHeight
    drawer.setLayoutParams(new LayoutParams(width, MATCH_PARENT, Gravity.START)) // set drawer width
    drawer.setBackgroundColor(R.color.my_tran_dark_blue2.r2Color)
    drawer.setAdapter(adapter)
    drawer.onItemClick((_: AdapterView[_], _: View, position: Int, id: Long) => {
      val selectedDrawerItem = drawerItems(position - 1) // minus 1 to compensate for adding a header
      presenter.handleDrawerItemClicked(selectedDrawerItem, currentDrawerItem == selectedDrawerItem)
    })

    // 16:9 ratio - drawer width to header height
    val height = (9.0 / 16.0) * width
    val header = layoutInflater.inflate(R.layout.side_menu_header, null)
    header.setLayoutParams(new AbsListView.LayoutParams(MATCH_PARENT, height.toInt)) // set header height
    drawer.addHeaderView(header)

    // setup button to control setting online/away/offline status
    presenceBtn.setOnMenuButtonClickListener(new OnMenuButtonClick {
      override def onClick(menuButton: MenuButton): Unit = {
        menuButton match {
          case LEFT  => presenter.handleChangePresence(Online)
          case MID   => presenter.handleChangePresence(Away)
          case RIGHT => presenter.handleChangePresence(Offline)
        }
        presenceBtn.getButtonMenu.toggle()
        Crouton.cancelAllCroutons()
      }
    })

    // setup button to edit status message
    find[ImageView](R.id.img_edit_status).onClick(showChangeStatusMsgDialog())
    statusMsgTextView.onClick(showChangeStatusMsgDialog())
  }

  def showChangeStatusMsgDialog(): Unit = {
    val view = View.inflate(ctx, R.layout.change_status_msg_dialog, null)
    val input = view.findViewById(R.id.et_status_msg).asInstanceOf[EditText]

    val dialog = new AlertDialog.Builder(ctx)
      .setView(view)
      .setPositiveButton("Ok", (dialog: DialogInterface, i: Int) ⇒ {presenter.handleChangeStatusMsg(input.getText.toString)})
      .setNegativeButton("Cancel", (dialog: DialogInterface, i: Int) ⇒ dialog.dismiss())
      .create()

    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.show()
    List(DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE).map(button ⇒ {
      dialog.getButton(button).setBackgroundColor(R.color.my_tran_dark_blue2.r2Color)
      dialog.getButton(button).setTextColor(R.color.my_orange.r2Color)
    })
  }

  def setStatusMsg(msg: String): Unit = statusMsgTextView.setText(msg)

  def setName(name: String): Unit = find[TextView](R.id.tv_name).setText(name)

  def setUserProfileIcon(username: String, regionId: String): Unit = {
    val profileIconImageView = find[ImageView](R.id.img_my_profile_icon)
    SummonerUtils.loadProfileIcon(username, regionId, profileIconImageView, 55)
  }

  def switchToOnline(): Unit = presenceBtn.setImageDrawable(R.drawable.circle_online)

  def switchToAway(): Unit = presenceBtn.setImageDrawable(R.drawable.circle_away)

  def switchToOffline(): Unit = presenceBtn.setImageDrawable(R.drawable.circle_offline)

  def showIsOfflineMsg(): Unit = {
    val customStyle = new Style.Builder().setBackgroundColor(R.color.offline_warning).build()
    Crouton.makeText(ctx.asInstanceOf[ActionBarActivity], R.string.offline_mode_warning.r2String, customStyle)
      .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build())
      .show()
  }

  def showSettings(): Unit = {
    ctx.startActivity(new Intent(ctx, classOf[PreferenceSettings]))
  }

  def showLogoutConfirmation(): Unit = ConfirmDialog(
    msg = R.string.dialog_logout_message.r2String,
    code2run = presenter.handleLogout(),
    btnTitle = "Logout"
  ).show()

  def updateDrawer(selectedItem: DrawerItem): Unit = {
    currentDrawerItem.isSelected = false
    selectedItem.isSelected = true
    currentDrawerItem = selectedItem // update current with the selected
    adapter.updateData(drawerItems)
  }

  def isOpen: Boolean = isDrawerOpen(drawer)

  def openDrawer(): Unit = openDrawer(drawer)

  def closeDrawer(): Unit = closeDrawers()
}

object SideDrawerView {
  val Online  = 1
  val Offline = 2
  val Away    = 3
}