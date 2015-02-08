package com.thangiee.lolhangouts.ui.sidedrawer

import android.content.{Context, Intent}
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.DrawerLayout.LayoutParams
import android.support.v7.app.ActionBarActivity
import android.util.AttributeSet
import android.view._
import android.widget._
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.rengwuxian.materialedittext.MaterialEditText
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.repository._
import com.thangiee.lolhangouts.domain.interactor.{ChangeUserStatusCaseImpl, GetAppDataUseCaseImpl, GetUserUseCaseImpl, LogoutUseCaseImpl}
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.main.MainActivity
import com.thangiee.lolhangouts.ui.settings.SettingsActivity
import com.thangiee.lolhangouts.ui.sidedrawer.SideDrawerView._
import com.thangiee.lolhangouts.utils._
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu.MenuButton._
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu.{MenuButton, OnMenuButtonClick}
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay

class SideDrawerView(implicit ctx: Context, a: AttributeSet) extends DrawerLayout(ctx, a) with CustomView {
  lazy val drawer            = new ListView(ctx)
  lazy val adapter           = new DrawerItemAdapter()
  lazy val presenceBtn       = find[ExpandableMenuOverlay](R.id.btn_menu_presence)
  lazy val statusMsgTextView = find[TextView](R.id.tv_status_msg)

  override val presenter = new SideDrawerPresenter(this, GetAppDataUseCaseImpl(), ChangeUserStatusCaseImpl(), GetUserUseCaseImpl(), LogoutUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()

    addView(drawer)
    val width = screenAbsWidth - toolbarHeight
    drawer.setLayoutParams(new LayoutParams(width, MATCH_PARENT, Gravity.START)) // set drawer width
    drawer.setBackgroundColor(R.color.md_grey_50.r2Color)
    drawer.onItemClick((_: AdapterView[_], v: View, position: Int, id: Long) => delay(500) {
      val selectedDrawerItem = adapter.getItem(position - 1) // minus 1 to compensate for adding a header
      presenter.handleDrawerItemClicked(selectedDrawerItem, position - 1)
    })

    val header = layoutInflater.inflate(R.layout.side_menu_header, null)
    drawer.addHeaderView(header)
    drawer.setAdapter(adapter)

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
    val input = view.findViewById(R.id.et_status_msg).asInstanceOf[MaterialEditText]
    input.setFloatingLabelText("Current: " + statusMsgTextView.getText)

    new Builder(ctx)
      .title("Set Status Message")
      .positiveText("Change")
      .negativeText("Cancel")
      .onPositive((dialog) => presenter.handleChangeStatusMsg(input.txt2str))
      .customView(view, true)
      .show()
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
    Crouton.makeText(ctx.asInstanceOf[ActionBarActivity], R.string.offline_mode_warning.r2String, customStyle, R.id.crouton_holder)
      .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build())
      .show()
  }

  def showSettings(): Unit = {
    ctx.startActivity(new Intent(ctx, classOf[SettingsActivity]))
  }

  def showLogoutConfirmation(): Unit = {
    new Builder(ctx)
      .content(R.string.dialog_logout_message)
      .positiveText("Logout")
      .negativeText("Cancel")
      .onPositive((dialog) => presenter.handleLogout())
      .show()
  }

  def showRemoveAdsConfirmation() = ctx.asInstanceOf[MainActivity].setUpBilling()

  def updateDrawer(position: Int): Unit = {
    adapter.setCurrentDrawer(position)
    adapter.notifyDataSetChanged()
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