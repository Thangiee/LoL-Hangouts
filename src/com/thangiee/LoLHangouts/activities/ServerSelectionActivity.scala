package com.thangiee.LoLHangouts.activities

import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.{ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api._
import com.thangiee.LoLHangouts.utils.ExtractorImplicits
import org.scaloid.common.SContext

import scala.collection.JavaConverters._

class ServerSelectionActivity extends ListActivity with SContext with TActivity with ExtractorImplicits {
  val servers = List(NA, BR, EUNE, EUW, KR, LAN, LAS, OCE, RU, TR)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.server_selection_screen)

    val serverDictionary = new BindDictionary[Server]()
    serverDictionary.addStringField(R.id.tv_server_name, (item: Server) ⇒ item.name)

    serverDictionary.addStaticImageField(R.id.im_flag, new StaticImageLoader[Server] {
      override def loadImage(item: Server, imageView: ImageView, position: Int) = imageView.setImageResource(item.flag)
    })
    
    val adapter = new FunDapter[Server](this, servers.asJava, R.layout.server_item, serverDictionary)
    setListAdapter(adapter)
  }

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = {
    appCtx.selectedServer = servers(position)
    startActivity[LoginActivity]
  }
}

