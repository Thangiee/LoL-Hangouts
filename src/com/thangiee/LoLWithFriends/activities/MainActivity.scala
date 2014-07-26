package com.thangiee.LoLWithFriends.activities

import android.graphics.Color
import android.os.Bundle
import android.view.{Menu, MenuItem}
import android.widget.ListView
import com.ami.fundapter.extractors.StringExtractor
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.nostra13.universalimageloader.core.{ImageLoader, ImageLoaderConfiguration, DisplayImageOptions}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.fragments.ChatScreenFragment
import com.thangiee.LoLWithFriends.services.{ChatService, FriendListService}
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common.SActivity

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MainActivity extends SActivity {

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(R.layout.main_screen)

    initImageLoader()
    startService[FriendListService]
    startService[ChatService]

    val serverDictionary = new BindDictionary[String]()
    serverDictionary.addStringField(R.id.tv_server_name, new StringExtractor[String] {
      override def getStringValue(item: String, position: Int): String = item
    })

    val adapter = new FunDapter[String](this, List("one", "two").asJava, R.layout.server_item, serverDictionary)

    val listView = new ListView(this)
    listView.setAdapter(adapter)

    val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)
    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(listView)
    sideDrawer.getMenuView.setBackgroundColor(Color.WHITE)

    getFragmentManager.beginTransaction().add(R.id.screen_container, new ChatScreenFragment).commit()
  }

  override def onPause(): Unit = {
    super.onPause()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.exit, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    println(">>> stoping services")
    stopService[FriendListService]
    stopService[ChatService]
    Future {LoLChat.disconnect()}
    super.onOptionsItemSelected(item)
  }

  private def initImageLoader() {
    val options = new DisplayImageOptions.Builder()
      .cacheInMemory(true)
      .cacheOnDisk(true)
      .build()

    val config = new ImageLoaderConfiguration.Builder(ctx)
      .defaultDisplayImageOptions(options)
      .threadPoolSize(3)
      .build()

    ImageLoader.getInstance().init(config)
  }
}

