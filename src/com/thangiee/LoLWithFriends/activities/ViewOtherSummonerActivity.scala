package com.thangiee.LoLWithFriends.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.fragments.ProfileViewPagerFragment
import org.scaloid.common.SActivity

class ViewOtherSummonerActivity extends SActivity with UpButton with Ads {
  override lazy val layout: ViewGroup = find[LinearLayout](R.id.linear_layout)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.view_other_summoner)
    val summonerName = getIntent.getStringExtra("name-key")
    val region = getIntent.getStringExtra("region-key")
    val fragment = ProfileViewPagerFragment.newInstance(summonerName, region)
    getFragmentManager.beginTransaction().replace(R.id.container_summoner_profile, fragment).commit()
    setupAds()
  }
}
