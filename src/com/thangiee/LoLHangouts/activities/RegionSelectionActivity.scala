package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.View
import android.widget.{AdapterView, ImageView, ListView}
import com.ami.fundapter.interfaces.StaticImageLoader
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.balysv.materialmenu.MaterialMenuDrawable
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.repository.datasources.api.Keys
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.domain.entities._
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi

import scala.collection.JavaConverters._

class RegionSelectionActivity extends ActionBarActivity with TActivity with AdapterView.OnItemClickListener {
  val regions = List(NA, BR, EUNE, EUW, KR, LAN, LAS, OCE, RU, TR)

  override val layoutId = R.layout.region_selection_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(finish())

    val serverDictionary = new BindDictionary[Region]()
    serverDictionary.addStringField(R.id.tv_region_name, (item: Region) ⇒ item.name)

    serverDictionary.addStaticImageField(R.id.im_flag, new StaticImageLoader[Region] {
      override def loadImage(reg: Region, imageView: ImageView, position: Int) = imageView.setImageResource(getRegionFlag(reg))
    })

    val adapter = new FunDapter[Region](this, regions.asJava, R.layout.region_item, serverDictionary)

    val listView = find[ListView](android.R.id.list)
    listView.setAdapter(adapter)
    listView.setOnItemClickListener(this)
  }

  override def onItemClick(adapterView: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    val regionId = regions(position).id

    // set the default region and key for the riot api caller
    RiotApi.region(regionId)
    RiotApi.key(Keys.productionKey)

    // save the selected region to avoid needing this activity after restating the app
    PrefsCache.put(CacheKey.LoginRegionId → regionId)
    startActivity[com.thangiee.LoLHangouts.ui.login.LoginActivity]
  }

  private def getRegionFlag(region: Region): Int = {
    region match {
      case NA   => R.drawable.ic_na
      case BR   => R.drawable.ic_br
      case EUNE => R.drawable.ic_eune
      case EUW  => R.drawable.ic_euw
      case KR   => R.drawable.ic_south_korea
      case LAN  => R.drawable.ic_latamn
      case LAS  => R.drawable.ic_latams
      case OCE  => R.drawable.ic_oce
      case RU   => R.drawable.ic_ru
      case TR   => R.drawable.ic_tr
    }
  }

}

