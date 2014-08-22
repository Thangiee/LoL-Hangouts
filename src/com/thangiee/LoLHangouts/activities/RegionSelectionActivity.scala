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

class RegionSelectionActivity extends ListActivity with SContext with TActivity with ExtractorImplicits {
  val regions = List(NA, BR, EUNE, EUW, KR, LAN, LAS, OCE, RU, TR)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.region_selection_screen)

    val serverDictionary = new BindDictionary[Region]()
    serverDictionary.addStringField(R.id.tv_region_name, (item: Region) ⇒ item.name)

    serverDictionary.addStaticImageField(R.id.im_flag, new StaticImageLoader[Region] {
      override def loadImage(item: Region, imageView: ImageView, position: Int) = imageView.setImageResource(item.flag)
    })
    
    val adapter = new FunDapter[Region](this, regions.asJava, R.layout.region_item, serverDictionary)
    setListAdapter(adapter)
  }

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = {
    appCtx.selectedRegion = regions(position)
    startActivity[LoginActivity]
  }
}

