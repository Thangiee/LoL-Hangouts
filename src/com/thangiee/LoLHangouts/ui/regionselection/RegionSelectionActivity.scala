package com.thangiee.LoLHangouts.ui.regionselection

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable
import com.skocken.efficientadapter.lib.adapter.AbsViewHolderAdapter.OnItemClickListener
import com.skocken.efficientadapter.lib.adapter.{AbsViewHolderAdapter, SimpleAdapter}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.data.cache.PrefsCache
import com.thangiee.LoLHangouts.data.repository.datasources.api.Keys
import com.thangiee.LoLHangouts.data.repository.datasources.helper.CacheKey
import com.thangiee.LoLHangouts.domain.entities._
import com.thangiee.LoLHangouts.ui.core.TActivity
import com.thangiee.LoLHangouts.utils._
import thangiee.riotapi.core.RiotApi

import scala.collection.JavaConversions._

class RegionSelectionActivity extends ActionBarActivity with TActivity with OnItemClickListener[Region] {
  override val layoutId = R.layout.act_region_selection_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(finish())
    getSupportActionBar.setTitle("Regions")

    val recyclerView = find[RecyclerView](R.id.recycler_view)
    recyclerView.setLayoutManager(new LinearLayoutManager(this))
    recyclerView.setHasFixedSize(true)

    val regions = List(NA, BR, EUNE, EUW, KR, LAN, LAS, OCE, RU, TR)
    val adapter = new SimpleAdapter[Region](R.layout.region_item, classOf[RegionViewHolder], regions)
    recyclerView.setAdapter(adapter.asInstanceOf[RecyclerView.Adapter[RegionViewHolder]])

    adapter.setOnItemClickListener(this)
  }

  override def onItemClick(adapter: AbsViewHolderAdapter[Region], view: View, region: Region, p: Int): Unit = {
    delay(600) {
      // set the default region and key for the riot api caller
      RiotApi.region(region.id)
      RiotApi.key(Keys.productionKey)

      // save the selected region to avoid needing this activity after restating the app
      PrefsCache.put(CacheKey.LoginRegionId → region.id)
      startActivity[com.thangiee.LoLHangouts.ui.login.LoginActivity]
    }
  }
}


