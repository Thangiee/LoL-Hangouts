package com.thangiee.lolhangouts.ui.regionselection

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.Intent
import android.os.{Bundle, SystemClock}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable
import com.parse.ParseObject
import com.skocken.efficientadapter.lib.adapter.AbsViewHolderAdapter.OnItemClickListener
import com.skocken.efficientadapter.lib.adapter.{AbsViewHolderAdapter, SimpleAdapter}
import com.thangiee.lolchat.region._
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.data.datasources.api.Keys
import com.thangiee.lolhangouts.ui.core.TActivity
import com.thangiee.lolhangouts.ui.receivers.DeleteOldMsgReceiver
import com.thangiee.lolhangouts.ui.utils._
import thangiee.riotapi.core.RiotApi

import scala.collection.JavaConversions._

class RegionSelectionActivity extends AppCompatActivity with TActivity with OnItemClickListener[Region] {
  override val layoutId = R.layout.act_region_selection_screen
  override val snackBarHolderId = R.id.act_region_selection_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    // run once when first time opening app
    if (Cached.isFirstLaunch) {
      // setup alarm to delete msg older than a time period
      val millis = TimeUnit.DAYS.toMillis(3)
      val i = new Intent(ctx, classOf[DeleteOldMsgReceiver])
      i.putExtra(DeleteOldMsgReceiver.TIME_KEY, millis)
      val p = PendingIntent.getBroadcast(ctx, 0, i, 0)
      alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), TimeUnit.HOURS.toMillis(1), p)

      // track new installs
      val parseObj = new ParseObject("NewInstall")
      parseObj.put("deviceModel", android.os.Build.MODEL)
      parseObj.put("androidVersion", s"${android.os.Build.VERSION.CODENAME} - ${android.os.Build.VERSION.SDK_INT}")
      parseObj.saveInBackground()

      Cached.isFirstLaunch = false
    }

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
    delay(500) {
      // set the default region and key for the riot api caller
      RiotApi.regionId = region.id
      RiotApi.key = Keys.productionKey

      // save the selected region to avoid needing this activity after restating the app
      Cached.loginRegionId = region.id
      startActivity[com.thangiee.lolhangouts.ui.login.LoginActivity]
    }
  }
}


