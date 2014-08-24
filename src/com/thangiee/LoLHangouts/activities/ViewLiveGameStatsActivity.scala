package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.fragments.LiveGamePagerFragment

class ViewLiveGameStatsActivity extends TActivity with UpButton with Ads {
  override lazy val layout: ViewGroup = find[LinearLayout](R.id.linear_layout)
  override val AD_UNIT_ID: String = "ca-app-pub-4297755621988601/7264366775"

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_with_container)
    val summonerName = getIntent.getStringExtra("name-key")
    val region = getIntent.getStringExtra("region-key")
    val fragment = LiveGamePagerFragment.newInstance(summonerName, region)
    getFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
//    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
  }
}
