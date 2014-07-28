package com.thangiee.LoLWithFriends.activities

import android.graphics.Color
import android.os.{Bundle, Handler}
import android.view.{Menu, MenuItem}
import android.widget.ListView
import com.ami.fundapter.extractors.StringExtractor
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.nostra13.universalimageloader.core.{DisplayImageOptions, ImageLoader, ImageLoaderConfiguration}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.fragments.ChatScreenFragment
import com.thangiee.LoLWithFriends.services.{ChatService, FriendListService}
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common._

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MainActivity extends SActivity {
  private var doubleBackToExitPressedOnce = false
  private lazy val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)

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

    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(listView)
    sideDrawer.getMenuView.setBackgroundColor(Color.WHITE)
    sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    sideDrawer.setDrawerIndicatorEnabled(true)

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
    item.getItemId match {
      case R.id.exit         => cleanUpAndDisconnect(); finish(); true;
      case android.R.id.home => sideDrawer.toggleMenu(); true
    }
    super.onOptionsItemSelected(item)
  }

  private def initImageLoader() {
    val options = new DisplayImageOptions.Builder()
      .cacheInMemory(true)
      .showImageOnFail(R.drawable.mlv__default_avatar)
      .build()

    val config = new ImageLoaderConfiguration.Builder(ctx)
      .threadPriority(Thread.NORM_PRIORITY - 2)
      .denyCacheImageMultipleSizesInMemory()
      .defaultDisplayImageOptions(options)
      .threadPoolSize(3)
      .build()

    ImageLoader.getInstance().init(config)
  }

  private def cleanUpAndDisconnect() {
    stopService[FriendListService]
    stopService[ChatService]
    Future {LoLChat.disconnect()}
  }

  // exit the app after quickly double clicking the back button
  override def onBackPressed(): Unit = {
    if (doubleBackToExitPressedOnce) {
      cleanUpAndDisconnect()
      super.onBackPressed()
      return
    }

    doubleBackToExitPressedOnce = true
    toast("Click BACK again to exit")
    new Handler().postDelayed(new Runnable {
      override def run(): Unit = doubleBackToExitPressedOnce = false
    }, 2000)
  }
}

