package com.thangiee.LoLHangouts.activities

import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.{ImageView, ListView}
import com.ami.fundapter.extractors.StringExtractor
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLHangouts.api._
import com.thangiee.LoLHangouts.{MyApp, R}
import org.scaloid.common.SContext

import scala.collection.JavaConverters._

class ServerSelection extends ListActivity with SContext {
  val servers = List(NA, BR, EUNE, EUW, KR, LAN, LAS, OCE, RU, TR)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.server_selection_screen)

    val serverDictionary = new BindDictionary[Server]()
    serverDictionary.addStringField(R.id.tv_server_name, new StringExtractor[Server] {
      override def getStringValue(item: Server, position: Int): String = item.name
    })

    serverDictionary.addStaticImageField(R.id.im_flag, new StaticImageLoader[Server] {
      override def loadImage(item: Server, imageView: ImageView, position: Int) = imageView.setImageResource(item.flag)
    })
    
    val adapter = new FunDapter[Server](this, servers.asJava, R.layout.server_item, serverDictionary)
    setListAdapter(adapter)
  }

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = {
    MyApp.selectedServer = servers(position)
    Riot.api.setRegion(MyApp.selectedServer.toString)
    startActivity[LoginActivity]
  }
}
