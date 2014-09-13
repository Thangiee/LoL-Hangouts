package com.thangiee.LoLHangouts.views

import android.app.{Activity, AlertDialog, Fragment}
import android.content.{Context, DialogInterface, Intent}
import android.view.{View, Window}
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget._
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.{LoginActivity, MainActivity, PreferenceSettings}
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.fragments.{BlankFragment, ChatScreenFragment, ProfileViewPagerFragment}
import com.thangiee.LoLHangouts.utils.{Events, ExtractorImplicits, SummonerUtils}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}
import info.hoang8f.android.segmented.SegmentedGroup
import net.simonvt.menudrawer.MenuDrawer
import net.simonvt.menudrawer.MenuDrawer.OnDrawerStateChangeListener
import org.scaloid.common._

import scala.collection.JavaConversions._

class SideDrawerView(implicit ctx: Context) extends RelativeLayout(ctx) with TView[SideDrawerView]
with AdapterView.OnItemClickListener with ExtractorImplicits {
  private val drawerItems = List(
    DrawerItem("Chat", R.drawable.ic_action_dialog, isSelected = true), // default selection
    DrawerItem("My Profile", R.drawable.ic_action_user),
    DrawerItem("Search Summoner", R.drawable.ic_action_search),
    DrawerItem("Live Game Stats", R.drawable.ic_action_monitor),
    DrawerItem("Settings", R.drawable.ic_action_settings),
    DrawerItem("Remove Ads", R.drawable.ic_action_like),
    DrawerItem("Logout", R.drawable.ic_action_exit))

  private var adapter: FunDapter[DrawerItem] = _
  private var currentDrawerItem = drawerItems(0)
  init()

  override def basis: SideDrawerView = this

  private def init() {
    layoutInflater(ctx).inflate(R.layout.side_menu, this)

    // set username and status message
    find[TextView](R.id.tv_username).setText(appCtx.currentUser)
    find[TextView](R.id.tv_status_msg).setText(LoLChat.statusMsg())

    // load account icon
    val iconImageView = find[ImageView](R.id.img_my_profile_icon)
    SummonerUtils.loadProfileIcon(appCtx.currentUser, appCtx.selectedRegion.id, iconImageView, 55)

    // setup button to edit status message
    val editStatusBtn = find[ImageView](R.id.img_edit_status)
    editStatusBtn.setOnClickListener((v: View) ⇒ showChangeStatusMsgDialog())

    // setup the radio group and button to control online/away/offline status
    val seg = find[SegmentedGroup](R.id.seg_presence)
    seg.setTintColor(R.color.status_available.r2Color)
    seg.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(group: RadioGroup, checkedId: Int): Unit = {
        val onlineBtn = find[RadioButton](R.id.btn_online)
        val awayBtn = find[RadioButton](R.id.btn_away)
        if (onlineBtn.isChecked) {
          seg.setTintColor(R.color.status_available.r2Color)
          LoLChat.appearOnline()
          Crouton.cancelAllCroutons()
        } else if (awayBtn.isChecked){
          seg.setTintColor(R.color.status_away.r2Color)
          LoLChat.appearAway()
          Crouton.cancelAllCroutons()
        } else {
          seg.setTintColor(R.color.status_offline.r2Color)
          LoLChat.appearOffline()
          // show warning when in offline mode
          val customStyle = new Style.Builder().setBackgroundColor(R.color.offline_warning).build()
          Crouton.makeText(ctx.asInstanceOf[Activity], R.string.offline_mode_warning.r2String, customStyle)
            .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build()).show()
        }
      }
    })

    val offlineBtn = find[RadioButton](R.id.btn_offline)
    if (Prefs.getBoolean("offline-login", false)) offlineBtn.setChecked(true)

    setupListView()
  }

  private def setupListView() {
    val menuDictionary = new BindDictionary[DrawerItem]()

    // drawer menu item title
    menuDictionary.addStringField(R.id.tv_menu_item_name, (item: DrawerItem) ⇒ item.title)
      .conditionalTextColor((item: DrawerItem) ⇒ item.isSelected, R.color.my_orange.r2Color, R.color.white.r2Color)

    // drawer menu item icon
    menuDictionary.addStaticImageField(R.id.img_drawer_item, new StaticImageLoader[DrawerItem] {
      override def loadImage(item: DrawerItem, imageView: ImageView, position: Int): Unit = imageView.setImageResource(item.icon)
    })

    adapter = new FunDapter[DrawerItem](ctx, drawerItems, R.layout.side_menu_item, menuDictionary)

    val listView = find[ListView](android.R.id.list)
    listView.setAdapter(adapter)
    listView.setOnItemClickListener(this)
  }

  private def showChangeStatusMsgDialog(): Unit = {
    val view = View.inflate(ctx, R.layout.change_status_msg_dialog, null)
    val input = view.findViewById(R.id.et_status_msg).asInstanceOf[EditText]

    val dialog = new AlertDialog.Builder(ctx)
      .setView(view)
      .setPositiveButton("Ok", (dialog: DialogInterface, i: Int) ⇒ {
      LoLChat.changeStatusMsg(input.getText.toString)
      find[TextView](R.id.tv_status_msg).setText(input.getText)
    })
      .setNegativeButton("Cancel", (dialog: DialogInterface, i: Int) ⇒ dialog.dismiss())
      .create()

    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.show()
    List(DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE).map(button ⇒ {
      dialog.getButton(button).setBackgroundColor(R.color.my_tran_dark_blue2.r2Color)
      dialog.getButton(button).setTextColor(R.color.my_orange.r2Color)
    })
  }

  private def showLogoutDialog() = {
    new AlertDialogBuilder(R.string.dialog_logout_title, R.string.dialog_logout_message)
      .positiveButton(android.R.string.yes, (d: DialogInterface, i: Int) ⇒ {
      EventBus.getDefault.post(new Events.FinishMainActivity)
      ctx.startActivity(new Intent(ctx, classOf[LoginActivity]))
    })
      .negativeButton(android.R.string.no, (d: DialogInterface, i: Int) ⇒ d.dismiss())
      .show()
  }

  override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    val mainActivity = ctx.asInstanceOf[MainActivity]
    val drawer = mainActivity.sideDrawer
    var fragment: Fragment = new Fragment
    val selectedDrawerItem = drawerItems(position)

    drawer.closeMenu()
    selectedDrawerItem.title match {
      case "Chat" ⇒ fragment = new ChatScreenFragment
      case "My Profile" ⇒ fragment = ProfileViewPagerFragment.newInstance(appCtx.currentUser, appCtx.selectedRegion.id)
      case "Search Summoner" ⇒ fragment = BlankFragment.withSummonerSearch()
      case "Live Game Stats" ⇒ fragment = BlankFragment.withLiveGameSearch(appCtx.currentUser)
      case "Settings" ⇒ ctx.startActivity(new Intent(ctx, classOf[PreferenceSettings])); return
      case "Remove Ads" ⇒ mainActivity.setUpBilling(); return
      case "Logout" ⇒ showLogoutDialog(); return
    }

    //update the text color of the selected menu item in the nav drawer
    currentDrawerItem.isSelected = false
    selectedDrawerItem.isSelected = true
    adapter.updateData(drawerItems)

    drawer.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener {
      override def onDrawerStateChange(oldState: Int, newState: Int): Unit = {
        // wait til drawer close animation complete and check that the selected drawer item
        // is not the same as the current one before changing fragment.
        if (newState == MenuDrawer.STATE_CLOSED && selectedDrawerItem != currentDrawerItem) {
          val transaction = ctx.asInstanceOf[Activity].getFragmentManager.beginTransaction()
          transaction.replace(R.id.screen_container, fragment).commit()

          currentDrawerItem = selectedDrawerItem // update current with the selected
        }
      }

      override def onDrawerSlide(p1: Float, p2: Int): Unit = {}
    })
  }

  case class DrawerItem(title: String, icon: Int, var isSelected: Boolean = false)
}
