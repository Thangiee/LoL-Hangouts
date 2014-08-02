package com.thangiee.LoLWithFriends.views

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget._
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{FunDapter, BindDictionary}
import com.ami.fundapter.extractors.StringExtractor
import com.squareup.picasso.Picasso
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.SummonerUtils
import com.thangiee.LoLWithFriends.{MyApp, R}
import info.hoang8f.android.segmented.SegmentedGroup
import org.scaloid.common.{SystemService, TraitView}
import scala.collection.JavaConversions._

class SideDrawerView(implicit ctx: Context) extends RelativeLayout(ctx) with TraitView[SideDrawerView]
  with SystemService with AdapterView.OnItemClickListener {
  val drawerItems = List(DrawerItem("Chat", R.drawable.ic_action_dialog),
    DrawerItem("My Profile", R.drawable.ic_action_user))

  init()

  override def basis: SideDrawerView = this

  private def init() {
    layoutInflater(ctx).inflate(R.layout.side_menu, this)

    // set username and status message
    find[TextView](R.id.tv_username).setText(MyApp.currentUser)
    find[TextView](R.id.tv_status_msg).setText(LoLChat.statusMsg())

    // load account icon
    val iconImageView = find[ImageView](R.id.img_my_profile_icon)
    Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(MyApp.currentUser, MyApp.selectedServer)).into(iconImageView)

    // setup button to edit status message
    val editStatusBtn = find[ImageView](R.id.img_edit_status)
    editStatusBtn.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {} //todo: to be implement
    })

    // setup the radio group and button to control online/away status
    val seg = find[SegmentedGroup](R.id.seg_presence)
    seg.setTintColor(getResources.getColor(R.color.status_available))
    seg.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(group: RadioGroup, checkedId: Int): Unit = {
        val onlineBtn = find[RadioButton](R.id.btn_online)
        if (onlineBtn.isChecked) {
          seg.setTintColor(getResources.getColor(R.color.status_available))
          LoLChat.appearOnline()
        } else {
          seg.setTintColor(getResources.getColor(R.color.status_away))
          LoLChat.appearAway()
        }
      }
    })

    setupListView()
  }

  private def setupListView() {
    val serverDictionary = new BindDictionary[DrawerItem]()

    // drawer menu item title
    serverDictionary.addStringField(R.id.tv_menu_item_name, new StringExtractor[DrawerItem] {
      override def getStringValue(item: DrawerItem, position: Int): String = item.title
    })

    // drawer menu item icon
    serverDictionary.addStaticImageField(R.id.img_drawer_item, new StaticImageLoader[DrawerItem] {
      override def loadImage(item: DrawerItem, imageView: ImageView, position: Int): Unit = imageView.setImageResource(item.icon)
    })

    val adapter = new FunDapter[DrawerItem](ctx, drawerItems, R.layout.side_menu_item, serverDictionary)

    val listView = find[ListView](android.R.id.list)
    listView.setAdapter(adapter)
    listView.setOnItemClickListener(this)
  }

  override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    drawerItems(position).title match {
      case "Chat" =>
      case "My Profile" =>
    }
  }

  case class DrawerItem(title: String, icon: Int)
}
