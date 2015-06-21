package com.thangiee.lolhangouts.ui.profile

import android.content.{Context, Intent}
import android.os.Bundle
import android.view.{Menu, MenuItem, ViewGroup}
import android.widget.LinearLayout
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.Cached
import com.thangiee.lolhangouts.ui.core.{TActivity, TIntent, UpButton, Ads}

case class ViewProfileActivity() extends TActivity with UpButton with Ads {
  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.ads_holder)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/6324061176"
  override      val layoutId              = R.layout.act_with_container

  private lazy val summonerName = getIntent.getStringExtra("name-key")
  private lazy val regionId = getIntent.getStringExtra("region-key")
  private lazy val contentContainer = find[LinearLayout](R.id.content_container)
  private lazy val container = new ProfileContainer(summonerName, regionId)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    contentContainer.addView(container.getView)

    if (Cached.isAdsEnable) setupAds()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    if (container.onCreateOptionsMenu(getMenuInflater, menu)) true
    else super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (container.onOptionsItemSelected(item)) true
    else super.onOptionsItemSelected(item)
  }
}

object ViewProfileActivity extends TIntent {
  def apply(name: String, regionId: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[ViewProfileActivity]).args("name-key" → name, "region-key" → regionId)
  }
}
