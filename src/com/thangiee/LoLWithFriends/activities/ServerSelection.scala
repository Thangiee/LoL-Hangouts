package com.thangiee.LoLWithFriends.activities

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.{ImageView, ListView}
import com.ami.fundapter.extractors.StringExtractor
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api._
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
    val intent = new Intent(this, classOf[LoginActivity])
    intent.putExtra("server-url", servers(position).url)
    intent.putExtra("server-name", servers(position).name)
    intent.putExtra("server-flag", servers(position).flag)
    startActivity(intent)
  }
}

